package ru.gavrilovegor519.nodehistj.util;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
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
