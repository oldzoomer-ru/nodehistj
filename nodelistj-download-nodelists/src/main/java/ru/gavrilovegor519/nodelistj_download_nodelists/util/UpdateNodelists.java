package ru.gavrilovegor519.nodelistj_download_nodelists.util;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
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
    private final MinioClient minioClient;
    private final FtpClient ftpClient;
    private final KafkaTemplate<String, List<String>> kafkaTemplate;

    @Value("${ftp.path}")
    private String ftpPath;

    @Value("${ftp.downloadFromYear}")
    private int downloadFromYear;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void updateNodelists() throws IOException {
        try {
            createBucket();
            ftpClient.open();

            for (int i = Year.now().getValue(); i >= downloadFromYear; i--) {
                List<String> objects = new ArrayList<>();
                String[] listFiles = ftpClient.listFiles(ftpPath + i);

                for (String file : listFiles) {
                    if (file.matches(ftpPath + "\\d{4}/nodelist\\.\\d{3}") && !isObjectExist(file)) {
                        try (ByteArrayOutputStream byteArrayOutputStream = ftpClient.downloadFile(file)) {
                            minioClient.putObject(PutObjectArgs.builder().bucket("nodehist").object(file)
                                    .stream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                                            byteArrayOutputStream.size(), -1).build());
                            objects.add(file);
                        }
                    }
                }

                if (!objects.isEmpty()) {
                    kafkaTemplate.send("download_nodelists_is_finished_topic", objects);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ftpClient.close();
        }
    }

    public void createBucket() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket("nodehist").build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("nodehist").build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isObjectExist(String name) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket("nodehist")
                    .object(name).build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
