package ru.oldzoomer.minio.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for S3 connection.
 * This class holds the properties required to connect to a S3 server.
 */
@ConfigurationProperties(prefix = "s3")
@RequiredArgsConstructor
@Getter
public class S3ConnectionProperties {
    /**
     * The URL of the S3 server.
     */
    private final String url;

    /**
     * The region of the S3 server.
     */
    private final String region;

    /**
     * The access key for the S3 server.
     */
    private final String accessKey;

    /**
     * The secret key for the S3 server.
     */
    private final String secretKey;
}
