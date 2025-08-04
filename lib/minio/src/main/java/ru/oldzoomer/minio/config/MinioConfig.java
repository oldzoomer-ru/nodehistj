package ru.oldzoomer.minio.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
@EnableConfigurationProperties(MinioConnectionProperties.class)
public class MinioConfig {
    @Bean
    MinioClient minioClient(MinioConnectionProperties minioConnectionProperties) {
        return MinioClient.builder()
                .endpoint(minioConnectionProperties.getUrl())
                .credentials(minioConnectionProperties.getAccessKey(),
                        minioConnectionProperties.getSecretKey())
                .build();
    }
}
