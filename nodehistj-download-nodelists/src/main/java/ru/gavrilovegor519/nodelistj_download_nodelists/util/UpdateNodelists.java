package ru.gavrilovegor519.nodelistj_download_nodelists.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@EnableScheduling
public class UpdateNodelists {
    private final MinioUtils minioUtils;
    private final FtpClient ftpClient;
    private final KafkaTemplate<String, List<String>> kafkaTemplate;

    @Value("${ftp.path}")
    private String ftpPath;

    @Value("${ftp.downloadFromYear}")
    private int downloadFromYear;

    @Value("${minio.bucket}")
    private String bucket;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void updateNodelists() throws IOException {
        try {
            minioUtils.createBucket(bucket);
            ftpClient.open();

            for (int i = Year.now().getValue(); i >= downloadFromYear; i--) {
                List<String> objects = new ArrayList<>();
                String[] listFiles = ftpClient.listFiles(ftpPath + i);

                for (String file : listFiles) {
                    if (file.matches(ftpPath + "\\d{4}/nodelist\\.\\d{3}") &&
                            !minioUtils.isObjectExist(bucket, file)) {
                        try (ByteArrayOutputStream byteArrayOutputStream = ftpClient.downloadFile(file)) {
                            minioUtils.putObject(bucket, file, byteArrayOutputStream);
                            objects.add(file);
                        }
                    }
                }

                if (!objects.isEmpty()) {
                    kafkaTemplate.send("download_nodelists_is_finished_topic", 0,
                            "added_nodelists", objects);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ftpClient.close();
        }
    }
}
