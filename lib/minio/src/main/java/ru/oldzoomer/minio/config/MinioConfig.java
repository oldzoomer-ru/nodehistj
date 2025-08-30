package ru.oldzoomer.minio.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

/**
 * Configuration class for MinIO client.
 * This class provides a bean for the MinIO client, which is used to interact with MinIO object storage.
 */
@Configuration
@EnableConfigurationProperties(MinioConnectionProperties.class)
public class MinioConfig {
    /**
     * Creates a MinIO client bean.
     *
     * @param minioConnectionProperties the MinIO connection properties
     * @return a MinIO client configured with the provided connection properties
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
