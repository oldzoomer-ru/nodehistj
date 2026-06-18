package ru.oldzoomer.nodehistj.s3.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;

class S3ConfigTest {

    @Test
    void s3Client_shouldReturnNonNullClient() {
        // Given
        var config = new S3Config();
        var properties = new S3ConnectionProperties(
            "http://localhost:9000",
            "us-east-1",
            "test-access-key",
            "test-secret-key",
            true
        );

        // When
        S3Client client = config.s3Client(properties);

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void s3ConnectionProperties_shouldBindValues() {
        // Given
        var properties = new S3ConnectionProperties(
            "http://minio:9000",
            "eu-west-1",
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            false
        );

        // Then
        assertThat(properties.getUrl()).isEqualTo("http://minio:9000");
        assertThat(properties.getRegion()).isEqualTo("eu-west-1");
        assertThat(properties.getAccessKey()).isEqualTo("AKIAIOSFODNN7EXAMPLE");
        assertThat(properties.getSecretKey()).isEqualTo("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
        assertThat(properties.isPathStyleUrl()).isFalse();
    }
}
