package ru.oldzoomer.minio.utils;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for interacting with MinIO object storage.
 * Provides methods for retrieving, creating, checking, and uploading objects to MinIO.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class MinioUtils implements DisposableBean {

    private final MinioClient minioClient;

    /**
     * Retrieves an object from MinIO.
     *
     * @param bucketName the name of the bucket
     * @param object the name of the object
     * @return an InputStream containing the object data
     * @throws RuntimeException if an error occurs while retrieving the object
     */
    public InputStream getObject(String bucketName, String object) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                    InvalidResponseException | ServerException | XmlParserException | IOException |
                    IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Error getting object {}/{}", bucketName, object, e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates a new bucket in MinIO if it does not already exist.
     *
     * @param bucketName the name of the bucket to create
     * @throws RuntimeException if an error occurs while creating the bucket
     */
    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                    InvalidResponseException | ServerException | XmlParserException | IOException |
                    IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Error creating bucket {}", bucketName, e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Checks if an object exists in a bucket.
     *
     * @param bucketName the name of the bucket
     * @param object the name of the object
     * @return true if the object exists, false otherwise
     */
    public boolean isObjectExist(String bucketName, String object) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(object)
                    .build());
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                    InvalidResponseException | ServerException | XmlParserException | IOException |
                 IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException _) {
            return false;
        }
    }

    /**
     * Uploads an object to a bucket in MinIO.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @param stream the ByteArrayOutputStream containing the object data
     * @throws RuntimeException if an error occurs while uploading the object
     */
    public void putObject(String bucketName, String objectName, ByteArrayOutputStream stream) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(stream.toByteArray()), stream.size(), -1)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                    InvalidResponseException | ServerException | XmlParserException | IOException |
                    IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Error putting object {}/{}", bucketName, objectName, e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Closes the MinIO client.
     *
     * @throws Exception if an error occurs while closing the client
     */
    @Override
    public void destroy() throws Exception {
        minioClient.close();
    }
}
