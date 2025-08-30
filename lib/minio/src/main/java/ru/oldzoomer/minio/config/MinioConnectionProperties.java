package ru.oldzoomer.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Configuration properties for MinIO connection.
 * This class holds the properties required to connect to a MinIO server.
 */
@ConfigurationProperties(prefix = "minio")
@RequiredArgsConstructor
@Getter
public class MinioConnectionProperties {
    /**
     * The URL of the MinIO server.
     */
    private final String url;

    /**
     * The access key for the MinIO server.
     */
    private final String accessKey;

    /**
     * The secret key for the MinIO server.
     */
    private final String secretKey;
}
