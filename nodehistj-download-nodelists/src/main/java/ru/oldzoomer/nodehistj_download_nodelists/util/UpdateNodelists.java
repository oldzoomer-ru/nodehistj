package ru.oldzoomer.nodehistj_download_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.oldzoomer.minio.utils.MinioUtils;
import ru.oldzoomer.nodehistj_download_nodelists.exception.NodelistUpdateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for downloading and updating nodelist files from FTP server.
 * <p>
 * Responsibilities:
 * - Downloads nodelist files from FTP server
 * - Stores files in MinIO object storage
 * - Sends notifications about new files via Kafka
 * - Manages scheduled updates of nodelist files
 * <p>
 * This service handles the complete workflow of downloading nodelist files,
 * processing them, and notifying other services about new files.
 * <p>
 * The service is scheduled to run periodically (default every 24 hours) to
 * ensure that the nodelist files are always up-to-date. It processes files
 * for the current year and previous years starting from the configured
 * downloadFromYear.
 */
@RequiredArgsConstructor
@Component
@EnableScheduling
@Slf4j
@Profile("!test")
public class UpdateNodelists {
    private final MinioUtils minioUtils;
    private final FtpClient ftpClient;
    private final KafkaTemplate<String, List<String>> kafkaTemplate;

    @Value("${ftp.path}")
    private String ftpPath;

    @Value("${ftp.downloadFromYear}")
    private int downloadFromYear;

    @Value("${app.minio.bucket}")
    private String bucket;

    private final int currentYear = Year.now().getValue();

    private final List<String> downloadedFiles = new ArrayList<>();

    /**
     * Main method for updating nodelist files.
     * Runs on schedule (default every 24 hours).
     * Downloads files for current and previous years (starting from
     * downloadFromYear).
     *
     * @throws NodelistUpdateException if update error occurs
     */
    @Scheduled(fixedRateString = "${ftp.download.interval:86400000}") // 24h by default
    public void updateNodelists() {
        try {
            validateInputs();
            minioUtils.createBucket(bucket);
            ftpClient.open();

            for (int year = currentYear; year >= downloadFromYear; year--) {
                processYearFiles(year);
            }

            sendMessageToKafka();
        } catch (Exception e) {
            log.error("Failed to update nodelists", e);
            throw new NodelistUpdateException("Nodelist update failed", e);
        } finally {
            try {
                ftpClient.close();
            } catch (IOException e) {
                log.warn("Failed to close FTP connection", e);
            }
        }
    }

    /**
     * Processes nodelist files for specified year
     *
     * @param year year to process
     * @throws IOException if FTP operation fails
     */
    private void processYearFiles(int year) throws IOException {
        String yearPath = ftpPath + year + "/";
        List<String> newFiles = Arrays.stream(ftpClient.listFiles(yearPath))
                .filter(file -> file.matches(".*/nodelist\\.\\d{3}"))
                .filter(file -> !minioUtils.isObjectExist(bucket, normalizeObjectName(file)))
                .peek(file -> log.debug("Processing new file: {}", file))
                .toList();

        log.info("Found {} new files for year {}", newFiles.size(), year);

        newFiles.forEach(this::processFile);
    }

    /**
     * Processes single nodelist file: downloads from FTP and saves to MinIO
     *
     * @param filePath full path to file on FTP server
     */
    private void processFile(String filePath) {
        try (ByteArrayOutputStream byteArrayOutputStream = ftpClient.downloadFile(filePath)) {
            String objectName = normalizeObjectName(filePath);
            minioUtils.putObject(bucket, objectName, byteArrayOutputStream);
            downloadedFiles.add(objectName);
        } catch (Exception e) {
            log.error("Error of upload nodelist to Minio, or download nodelist from FTP", e);
            // continue processing other files
        }
    }

    private void sendMessageToKafka() {
        log.info("Sending {} new files information to Kafka", downloadedFiles.size());

        // Корректная отправка с явным логированием результата и ошибок.
        kafkaTemplate.send("download_nodelists_is_finished_topic", downloadedFiles)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(
                                "Failed to send message to Kafka. Size: {}. Topic: {}",
                                downloadedFiles.size(),
                                "download_nodelists_is_finished_topic",
                                ex);
                    } else if (result != null && result.getRecordMetadata() != null) {
                        var md = result.getRecordMetadata();
                        log.info(
                                "Kafka send OK: topic={}, partition={}, offset={}",
                                md.topic(),
                                md.partition(),
                                md.offset());
                    } else {
                        log.warn(
                                "Kafka send finished without exception but without metadata (topic={})",
                                "download_nodelists_is_finished_topic");
                    }
                });
    }

    /**
     * Normalizes object name by removing leading slash if present
     *
     * @param objectName object name to normalize
     * @return normalized object name
     */
    private String normalizeObjectName(String objectName) {
        if (objectName.charAt(0) == '/') {
            objectName = objectName.substring(1);
        }
        return objectName;
    }

    /**
     * Validates required configuration parameters
     *
     * @throws IllegalArgumentException if parameters are not set
     */
    private void validateInputs() {
        if (ftpPath == null || ftpPath.isEmpty()) {
            throw new IllegalArgumentException("FTP path cannot be empty");
        }
        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException("Minio bucket cannot be empty");
        }

        if (downloadFromYear > currentYear) {
            throw new IllegalArgumentException("Download from year cannot be greater than current year");
        }
    }
}
