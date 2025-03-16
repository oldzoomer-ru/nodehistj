package ru.gavrilovegor519.nodelistj_download_nodelists.util;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

    public void putObject(String bucketName, String object, ByteArrayOutputStream byteArrayOutputStream) {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(object)
                    .stream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                            byteArrayOutputStream.size(), -1).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isObjectExist(String bucketName, String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName).object(objectName).build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        minioClient.close();
    }
}
