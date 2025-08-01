package ru.oldzoomer.minio.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.oldzoomer.minio.MinioUtils;

@Configuration
public class MinioUtilsConfiguration {
    @Bean
    @ConditionalOnClass(MinioUtils.class)
    MinioUtils minioUtils(
        MinioConnectionProperties minioConnectionProperties
    ) {
        return new MinioUtils(minioConnectionProperties);
    }
}
