package ru.oldzoomer.common.utils;

import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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

    public void createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isObjectExist(String bucketName, String object) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(object)
                .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void putObject(String bucketName, String objectName, ByteArrayOutputStream stream) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(new ByteArrayInputStream(stream.toByteArray()), stream.size(), -1)
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        minioClient.close();
    }
}