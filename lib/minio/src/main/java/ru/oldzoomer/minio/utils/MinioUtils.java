package ru.oldzoomer.minio.utils;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Utility class for interacting with MinIO object storage.
 * Provides methods for retrieving, creating, checking, and uploading objects to MinIO.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MinioUtils implements DisposableBean {

    private final MinioClient minioClient;

    public InputStream getObject(String bucketName, String object) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("Error getting object {}/{}", bucketName, object, e);
            throw new RuntimeException(e);
        }
    }

    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Error creating bucket {}", bucketName, e);
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
            log.error("Error putting object {}/{}", bucketName, objectName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        minioClient.close();
    }
}
