package ru.oldzoomer.minio.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

/**
 * Configuration class for MinIO client.
 * <p>
 * Responsible for:
 * - Creating MinIO client instance
 * - Configuring MinIO connection properties
 * - Setting up MinIO client with credentials
 */
@Configuration
@EnableConfigurationProperties(MinioConnectionProperties.class)
public class MinioConfig {
    /**
     * Creates a MinIO client instance.
     *
     * @param minioConnectionProperties MinIO connection properties
     * @return MinioClient instance configured with connection properties
     */
    @Bean
    MinioClient minioClient(MinioConnectionProperties minioConnectionProperties) {
        return MinioClient.builder()
                .endpoint(minioConnectionProperties.getUrl())
                .credentials(minioConnectionProperties.getAccessKey(),
                        minioConnectionProperties.getSecretKey())
                .build();
    }
}
