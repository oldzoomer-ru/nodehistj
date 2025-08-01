package ru.oldzoomer.minio.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConnectionConfiguration {
    @Bean
    @ConfigurationProperties("minio")
    @ConditionalOnProperty(prefix = "minio", name = "url")
    MinioConnectionProperties minioConnectionProperties() {
        return new MinioConnectionProperties();
    }
}
