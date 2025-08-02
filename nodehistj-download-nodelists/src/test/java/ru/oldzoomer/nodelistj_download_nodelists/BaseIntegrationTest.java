package ru.oldzoomer.nodelistj_download_nodelists;

import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("apache/kafka"))
            .waitingFor(Wait.forListeningPort());

    @SuppressWarnings("resource")
    @Container
    public static final MinIOContainer minioContainer = new MinIOContainer(
            DockerImageName.parse("minio/minio"))
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.user", minioContainer::getUserName);
        registry.add("minio.password", minioContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @AfterAll
    static void close() {
        minioContainer.close();
        kafkaContainer.close();
    }
}
