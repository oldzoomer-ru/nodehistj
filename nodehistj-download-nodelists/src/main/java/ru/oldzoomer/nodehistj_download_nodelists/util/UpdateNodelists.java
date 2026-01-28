package ru.oldzoomer.nodehistj_download_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
            List<String> downloadedFiles = new ArrayList<>(); // Local list for this execution
            int currentYear = Year.now().getValue(); // Local current year for this execution

            for (int year = currentYear; year >= downloadFromYear; year--) {
                int filesInYear = processYearFiles(year, downloadedFiles);
                totalFiles += filesInYear;
                processedYears++;
            }

            log.info("Processed {} years, found {} new files", processedYears, totalFiles);
            sendMessageToKafka(downloadedFiles);
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
            }
        }
    }

    /**
     * Processes nodelist files for specified year
     *
     * @param year year to process
     * @param downloadedFiles list to collect newly processed files
     * @throws IOException if FTP operation fails
     */
    private int processYearFiles(int year, List<String> downloadedFiles) throws IOException {
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

        newFiles.forEach(file -> processFile(file, downloadedFiles));
        return newFiles.size();
    }

    /**
     * Processes single nodelist file: downloads from FTP and saves to MinIO
     *
     * @param filePath full path to file on FTP server
     * @param downloadedFiles list to collect newly processed files
     */
    private void processFile(String filePath, List<String> downloadedFiles) {
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

    private void sendMessageToKafka(List<String> downloadedFiles) {
        log.info("Sending {} new files information to Kafka", downloadedFiles.size());

        // Only send non-empty messages to Kafka
        if (downloadedFiles.isEmpty()) {
            log.debug("No new files to send to Kafka, skipping message sending");
            return;
        }

        // Proper sending with explicit logging of result and errors
        kafkaTemplate.send("download_nodelists_is_finished_topic", downloadedFiles);
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

        if (downloadFromYear > Year.now().getValue()) {
            throw new IllegalArgumentException("Download from year (" + downloadFromYear +
                    ") cannot be greater than current year (" + Year.now().getValue() + ")");
        }

        if (downloadFromYear < 1980) {
            throw new IllegalArgumentException("Download from year (" + downloadFromYear +
                    ") cannot be less than 1980");
        }

        log.debug("Input parameters validated successfully");
    }
}