package ru.oldzoomer.minio.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

/**
 * Utility class for interacting with MinIO object storage.
 * Provides methods for retrieving, creating, checking, and uploading objects to MinIO.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class S3Utils implements DisposableBean {

    private final S3Client s3Client;

    /**
     * Retrieves an object from S3.
     *
     * @param bucketName the name of the bucket
     * @param object     the name of the object
     * @return an InputStream containing the object data
     */
    public InputStream getObject(String bucketName, String object) {
        return s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(object).build());
    }

    /**
     * Creates a new bucket in S3 if it does not already exist.
     *
     * @param bucketName the name of the bucket to create
     */
    public void createBucket(String bucketName) {
        if (!s3Client.headBucket(
                        HeadBucketRequest.builder().bucket(bucketName).build())
                .sdkHttpResponse().isSuccessful()) {
            s3Client.createBucket(
                    CreateBucketRequest.builder().bucket(bucketName).build());
        }
    }

    /**
     * Checks if an object exists in a bucket.
     *
     * @param bucketName the name of the bucket
     * @param object     the name of the object
     * @return true if the object exists, false otherwise
     */
    public boolean isObjectExist(String bucketName, String object) {
        return s3Client.headObject(
                        HeadObjectRequest.builder()
                                .bucket(bucketName).key(object).build())
                .sdkHttpResponse().isSuccessful();
    }

    /**
     * Uploads an object to a bucket in S3.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @param bytes      the array of bytes containing the object data
     */
    public void putObject(String bucketName, String objectName, byte[] bytes) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectName)
                        .build(),
                RequestBody.fromBytes(bytes));
    }

    /**
     * Closes the S3 client.
     */
    @Override
    public void destroy() {
        s3Client.close();
    }
}
