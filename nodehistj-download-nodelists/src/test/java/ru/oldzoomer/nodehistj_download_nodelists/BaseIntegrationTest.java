package ru.oldzoomer.nodehistj_download_nodelists;

import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.redpanda.RedpandaContainer;

@SpringBootTest
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    public static final RedpandaContainer redpandaContainer = new RedpandaContainer("redpandadata/redpanda")
            .waitingFor(Wait.forSuccessfulCommand("rpk cluster health"));

    @Container
    public static final MinIOContainer minioContainer = new MinIOContainer("minio/minio")
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .waitingFor(Wait.forSuccessfulCommand("mc ready local"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("s3.url", minioContainer::getS3URL);
        registry.add("s3.user", minioContainer::getUserName);
        registry.add("s3.password", minioContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", redpandaContainer::getBootstrapServers);
        registry.add("scheduling.enabled", () -> false);
        registry.add("app.updateAtStart", () -> false);
    }

    @AfterAll
    static void close() {
        minioContainer.close();
        redpandaContainer.close();
    }
}
