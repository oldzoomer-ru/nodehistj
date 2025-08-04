package ru.oldzoomer.minio.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.oldzoomer.minio.MinioUtils;

@Configuration
@ConditionalOnClass(MinioUtils.class)
@EnableConfigurationProperties(MinioConnectionProperties.class)
public class MinioConfig {
    @Bean
    MinioUtils minioUtils(MinioConnectionProperties minioConnectionProperties) {
        return new MinioUtils(minioConnectionProperties);
    }
}
