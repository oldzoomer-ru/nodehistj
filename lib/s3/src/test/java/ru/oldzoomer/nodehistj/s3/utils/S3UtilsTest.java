package ru.oldzoomer.nodehistj.s3.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3UtilsTest {

    private static final String BUCKET_NAME = "test-bucket";
    private static final String OBJECT_NAME = "test-object.txt";

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Utils s3Utils;

    @Test
    void getObject_shouldReturnInputStream() {
        // Given
        var expectedBytes = "test content".getBytes();
        var responseStream = new ResponseInputStream<>(
            GetObjectResponse.builder().build(),
            new ByteArrayInputStream(expectedBytes)
        );
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        // When
        var result = s3Utils.getObject(BUCKET_NAME, OBJECT_NAME);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(responseStream);
    }

    @Test
    void createBucket_shouldCreateBucket_whenBucketDoesNotExist() {
        // Given
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
            .thenThrow(NoSuchBucketException.builder().build());

        // When
        s3Utils.createBucket(BUCKET_NAME);

        // Then
        verify(s3Client).headBucket(any(HeadBucketRequest.class));
        verify(s3Client).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    void createBucket_shouldNotCreateBucket_whenBucketExists() {
        // Given
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
            .thenReturn(HeadBucketResponse.builder().build());

        // When
        s3Utils.createBucket(BUCKET_NAME);

        // Then
        verify(s3Client).headBucket(any(HeadBucketRequest.class));
        verify(s3Client, never()).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    void isObjectExist_shouldReturnTrue_whenObjectExists() {
        // Given
        when(s3Client.headObject(any(HeadObjectRequest.class)))
            .thenReturn(HeadObjectResponse.builder().build());

        // When
        var result = s3Utils.isObjectExist(BUCKET_NAME, OBJECT_NAME);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isObjectExist_shouldReturnFalse_whenObjectDoesNotExist() {
        // Given
        when(s3Client.headObject(any(HeadObjectRequest.class)))
            .thenThrow(NoSuchKeyException.builder().build());

        // When
        var result = s3Utils.isObjectExist(BUCKET_NAME, OBJECT_NAME);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void putObject_shouldUploadSuccessfully() {
        // Given
        var bytes = "test data".getBytes();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        // When
        s3Utils.putObject(BUCKET_NAME, OBJECT_NAME, bytes);

        // Then
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void destroy_shouldCloseS3Client() {
        // When
        s3Utils.destroy();

        // Then
        verify(s3Client).close();
    }
}
