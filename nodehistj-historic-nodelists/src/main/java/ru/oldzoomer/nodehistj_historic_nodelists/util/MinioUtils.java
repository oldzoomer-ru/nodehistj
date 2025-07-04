package ru.oldzoomer.nodehistj_historic_nodelists.util;

import java.io.InputStream;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;

@Component
@Profile("!test")
public class MinioUtils implements DisposableBean {
    private final MinioClient minioClient;

    public MinioUtils(@Value("${minio.url}") String minioUrl,
                      @Value("${minio.user}") String minioUser,
                      @Value("${minio.password}") String minioPassword) {
        minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioUser, minioPassword)
                .build();
    }

    public InputStream getObject(String bucketName, String object) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName).object(object).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        minioClient.close();
    }
}
