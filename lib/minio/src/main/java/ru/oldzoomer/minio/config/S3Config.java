package ru.oldzoomer.minio.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuration class for AWS S3 client.
 * This class provides a bean for the AWS S3 client, which is used to interact with AWS S3 object storage.
 */
@Configuration
@EnableConfigurationProperties(S3ConnectionProperties.class)
public class S3Config {
    /**
     * Creates an AWS S3 client bean.
     *
     * @param s3ConnectionProperties the S3 connection properties
     * @return an AWS S3 client configured with the provided connection properties
     */
    @Bean
    S3Client s3Client(S3ConnectionProperties s3ConnectionProperties) {
        return S3Client.builder()
                .endpointOverride(URI.create(s3ConnectionProperties.getUrl()))
                .forcePathStyle(s3ConnectionProperties.isPathStyleUrl())
                .region(Region.of(s3ConnectionProperties.getRegion()))
                .httpClientBuilder(ApacheHttpClient.builder())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                s3ConnectionProperties.getAccessKey(),
                                s3ConnectionProperties.getSecretKey())))
                .build();
    }
}
