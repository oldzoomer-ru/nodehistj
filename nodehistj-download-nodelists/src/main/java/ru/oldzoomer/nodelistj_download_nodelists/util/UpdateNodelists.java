package ru.oldzoomer.nodelistj_download_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.oldzoomer.minio.MinioUtils;
import ru.oldzoomer.nodelistj_download_nodelists.exception.NodelistUpdateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main service for downloading and updating nodelist files from FTP server.
 * Saves files to MinIO and sends notifications about new files via Kafka.
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

    /**
     * Main method for updating nodelist files.
     * Runs on schedule (default every 24 hours).
     * Downloads files for current and previous years (starting from downloadFromYear).
     * @throws NodelistUpdateException if update error occurs
     */
    @Scheduled(fixedRateString = "${ftp.download.interval:86400000}") // 24h by default
    public void updateNodelists() {
        try {
            validateInputs();
            minioUtils.createBucket(bucket);
            ftpClient.open();

            int currentYear = Year.now().getValue();
            if (downloadFromYear > currentYear) {
                log.warn("DownloadFromYear {} is in future", downloadFromYear);
                return;
            }
            for (int year = currentYear; year >= downloadFromYear; year--) {
                processYearFiles(year);
            }
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
     * @param year year to process
     * @throws IOException if FTP operation fails
     */
    private void processYearFiles(int year) throws IOException {
        String yearPath = ftpPath + year + "/";
        List<String> newFiles = Arrays.stream(ftpClient.listFiles(yearPath))
                .filter(file -> file.matches(yearPath + "nodelist\\.\\d{3}"))
                .filter(file -> !minioUtils.isObjectExist(bucket, file))
                .peek(file -> log.info("Processing new file: {}", file))
                .collect(Collectors.toList());

        newFiles.forEach(this::processFile);

        if (!newFiles.isEmpty()) {
            kafkaTemplate.send("download_nodelists_is_finished_topic",
                    String.valueOf(year), newFiles);
        }
    }

    /**
     * Processes single nodelist file: downloads from FTP and saves to MinIO
     * @param filePath full path to file on FTP server
     */
    private void processFile(String filePath) {
        try (ByteArrayOutputStream byteArrayOutputStream = ftpClient.downloadFile(filePath)) {
            if (filePath.charAt(0) == '/') {
                filePath = filePath.substring(1);
            }
            minioUtils.putObject(bucket, filePath, byteArrayOutputStream);
        } catch (Exception e) {
            log.error("Error of upload nodelist to Minio, or download nodelist from FTP", e);
        }
    }

    /**
     * Validates required configuration parameters
     * @throws IllegalArgumentException if parameters are not set
     */
    private void validateInputs() {
        if (ftpPath == null || ftpPath.isEmpty()) {
            throw new IllegalArgumentException("FTP path cannot be empty");
        }
        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException("Minio bucket cannot be empty");
        }
    }
}
