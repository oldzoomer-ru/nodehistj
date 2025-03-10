package ru.gavrilovegor519.nodehistj.config;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import ru.gavrilovegor519.Nodelist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class NodelistConfig {
    private final MinioClient minioClient;

    @Value("${minio.path}")
    private String minioPath;

    @Bean
    @RequestScope
    public Nodelist nodelist() {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        String dayOfYear = String.format("%03d", date.getDayOfYear());

        String path = minioPath + year + "/nodelist." + dayOfYear;

        try (InputStream inputStream = minioClient.getObject(GetObjectArgs
                .builder().bucket("nodehist").object(path).build())) {
            return new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
