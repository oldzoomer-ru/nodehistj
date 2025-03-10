package ru.gavrilovegor519.nodelistj_download_nodelists.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    private final String minioUrl;
    private final String minioUser;
    private final String minioPassword;

    public MinioConfig(@Value("${minio.url}") String minioUrl,
                       @Value("${minio.user}") String minioUser,
                       @Value("${minio.password}") String minioPassword) {
        this.minioUrl = minioUrl;
        this.minioUser = minioUser;
        this.minioPassword = minioPassword;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioUser, minioPassword)
                .build();
    }
}
