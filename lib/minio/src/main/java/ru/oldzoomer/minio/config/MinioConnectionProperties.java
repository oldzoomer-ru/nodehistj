package ru.oldzoomer.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ConfigurationProperties(prefix = "minio")
@RequiredArgsConstructor
@Getter
public class MinioConnectionProperties {
    private final String url;
    private final String accessKey;
    private final String secretKey;
}
