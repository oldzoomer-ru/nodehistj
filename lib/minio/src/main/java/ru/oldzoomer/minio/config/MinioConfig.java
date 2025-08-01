package ru.oldzoomer.minio.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import ru.oldzoomer.minio.MinioUtils;

@Configuration
@EnableConfigurationProperties(MinioConnectionProperties.class)
@AutoConfigureBefore(MinioUtils.class)
public class MinioConfig {
    
}
