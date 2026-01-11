package ru.oldzoomer.nodehistj_download_nodelists.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.minio.utils.MinioUtils;
import ru.oldzoomer.nodehistj_download_nodelists.exception.NodelistUpdateException;

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
 * <p>
 * This service is designed to be run in a non-test profile only, as indicated
 * by the @Profile("!test") annotation.
 */
@RequiredArgsConstructor
@Component
@EnableScheduling
@Log4j2
@Profile("!test")
public class UpdateNodelists {
    private final MinioUtils minioUtils;
    private final FtpClient ftpClient;
    private final KafkaTemplate<String, List<String>> kafkaTemplate;

    @Value("${ftp.path}")
    private String ftpPath;

    @Value("${ftp.download-from-year}")
    private int downloadFromYear;

    @Value("${app.minio.bucket}")
    private String bucket;

    private final int currentYear = Year.now().getValue();

    private final List<String> downloadedFiles = new ArrayList<>();

    /**
     * Main method for updating nodelist files.
     * Runs on schedule (at 03:00 UTC every day).
     * Downloads files for current and previous years (starting from
     * downloadFromYear).
     *
     * @throws NodelistUpdateException if update error occurs
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "UTC") // 03:00 UTC
    public void updateNodelists() {
        log.info("Starting nodelist update process");
        try {
            validateInputs();
            minioUtils.createBucket(bucket);

            ftpClient.open();

            int processedYears = 0;
            int totalFiles = 0;
            for (int year = currentYear; year >= downloadFromYear; year--) {
                int filesInYear = processYearFiles(year);
                totalFiles += filesInYear;
                processedYears++;
            }

            log.info("Processed {} years, found {} new files", processedYears, totalFiles);
            sendMessageToKafka();
        } catch (IOException e) {
            log.error("Failed to update nodelists due to IO error", e);
            throw new NodelistUpdateException("Nodelist update failed due to IO error", e);
        } catch (Exception e) {
            log.error("Unexpected error during nodelist update", e);
            throw new NodelistUpdateException("Nodelist update failed with unexpected error", e);
        } finally {
            try {
                ftpClient.close();
                log.debug("FTP connection closed successfully");
            } catch (IOException e) {
                log.error("Failed to close FTP connection due to IO error", e);
                throw new NodelistUpdateException("Nodelist update failed due to IO error", e);
            }
        }
    }

    /**
     * Processes nodelist files for specified year
     *
     * @param year year to process
     * @throws IOException if FTP operation fails
     */
    private int processYearFiles(int year) throws IOException {
        String yearPath = ftpPath + year + "/";
        String[] files = ftpClient.listFiles(yearPath);
        if (files == null || files.length == 0) {
            log.warn("No files found for year {}", year);
            return 0;
        }

        List<String> newFiles = Arrays.stream(files)
                .filter(file -> file.matches(".*/nodelist\\.\\d{3}"))
                .filter(file -> !minioUtils.isObjectExist(bucket, normalizeObjectName(file)))
                .toList();

        log.info("Found {} new files for year {}", newFiles.size(), year);

        newFiles.forEach(this::processFile);
        return newFiles.size();
    }

    /**
     * Processes single nodelist file: downloads from FTP and saves to MinIO
     *
     * @param filePath full path to file on FTP server
     */
    private void processFile(String filePath) {
        try (ByteArrayOutputStream byteArrayOutputStream = ftpClient.downloadFile(filePath)) {
            String objectName = normalizeObjectName(filePath);
            log.debug("Uploading file to MinIO: {}", objectName);
            minioUtils.putObject(bucket, objectName, byteArrayOutputStream);
            downloadedFiles.add(objectName);
            log.info("Successfully processed file: {}", objectName);
        } catch (IOException e) {
            log.error("IO error processing file {} - upload to MinIO or download from FTP", filePath, e);
            // continue processing other files
        } catch (Exception e) {
            log.error("Unexpected error processing file {} - upload to MinIO or download from FTP", filePath, e);
            // continue processing other files
        }
    }

    private void sendMessageToKafka() {
        log.info("Sending {} new files information to Kafka", downloadedFiles.size());

        // Only send non-empty messages to Kafka
        if (downloadedFiles == null || downloadedFiles.isEmpty()) {
            log.debug("No new files to send to Kafka, skipping message sending");
            return;
        }

        // Proper sending with explicit logging of result and errors
        kafkaTemplate.send("download_nodelists_is_finished_topic", downloadedFiles)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(
                                "Failed to send message to Kafka. Size: {}. Topic: {}",
                                downloadedFiles.size(),
                                "download_nodelists_is_finished_topic",
                                ex);
                    } else if (result != null) {
                        var recordMetadata = result.getRecordMetadata();
                        log.info(
                                "Kafka send OK: topic={}, partition={}, offset={}",
                                recordMetadata.topic(),
                                recordMetadata.partition(),
                                recordMetadata.offset());
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
        log.debug("Validating input parameters");
        if (ftpPath == null || ftpPath.isEmpty()) {
            throw new IllegalArgumentException("FTP path cannot be empty or null");
        }
        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException("Minio bucket cannot be empty or null");
        }

        if (downloadFromYear > currentYear) {
            throw new IllegalArgumentException("Download from year (" + downloadFromYear +
                    ") cannot be greater than current year (" + currentYear + ")");
        }

        if (downloadFromYear < 1980) {
            throw new IllegalArgumentException("Download from year (" + downloadFromYear +
                    ") cannot be less than 1980");
        }

        if (downloadFromYear < 0) {
            throw new IllegalArgumentException("Download from year (" + downloadFromYear +
                    ") cannot be negative");
        }

        log.debug("Input parameters validated successfully");
    }
}
