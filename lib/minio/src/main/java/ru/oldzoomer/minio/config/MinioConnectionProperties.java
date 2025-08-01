package ru.oldzoomer.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@ConfigurationProperties(prefix = "minio")
@Getter
public class MinioConnectionProperties {
    private final String url;
    private final String user;
    private final String password;

    public MinioConnectionProperties(
        String url, String user, String password
    ) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
}
