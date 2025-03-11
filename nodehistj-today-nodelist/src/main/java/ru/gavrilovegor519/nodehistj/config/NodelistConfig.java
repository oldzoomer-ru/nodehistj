package ru.gavrilovegor519.nodehistj.config;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.context.annotation.RequestScope;
import ru.gavrilovegor519.Nodelist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class NodelistConfig {
    private final MinioClient minioClient;
    private Nodelist nodelist;

    @Value("${minio.path}")
    private String minioPath;

    @Bean
    @RequestScope
    public Nodelist nodelist() {
        if (nodelist == null) {
            updateNodelist();
        }
        return nodelist;
    }

    @KafkaListener(topics = "download_nodelists_is_finished_topic", groupId = "nodelist_group")
    private void updateNodelist() {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        String dayOfYear = String.format("%03d", date.getDayOfYear());

        String path = minioPath + year + "/nodelist." + dayOfYear;

        try (InputStream inputStream = minioClient.getObject(GetObjectArgs
                .builder().bucket("nodehist").object(path).build())) {
            nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
            log.info("Updated nodelist");
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve nodelist from Minio", e);
        }
    }
}
