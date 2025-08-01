package ru.oldzoomer.nodehistj_history_diff;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@Transactional
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .waitingFor(Wait.forListeningPort());

    @SuppressWarnings("resource")
    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("apache/kafka"))
            .waitingFor(Wait.forListeningPort());

    @SuppressWarnings("resource")
    @Container
    public static MinIOContainer minioContainer = new MinIOContainer(
            DockerImageName.parse("minio/minio"))
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .waitingFor(Wait.forListeningPort());

    @SuppressWarnings("resource")
    @Container
    public static RedisContainer redisContainer = new RedisContainer(
            DockerImageName.parse("redis:8.0-alpine"))
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private NodeHistoryEntryRepository nodeHistoryEntryRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql:17-alpine:///testdb");
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.user", minioContainer::getUserName);
        registry.add("minio.password", minioContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.data.redis.host", redisContainer::getRedisHost);
        registry.add("spring.data.redis.port", redisContainer::getRedisPort);
    }

    @BeforeEach
    void setUpDatabase() {
        nodeHistoryEntryRepository.deleteAll();

        // Create test history entries
        NodeHistoryEntry addedEntry = new NodeHistoryEntry();
        addedEntry.setZone(1);
        addedEntry.setNetwork(1);
        addedEntry.setNode(1);
        addedEntry.setChangeDate(LocalDate.of(2023, 1, 1));
        addedEntry.setNodelistYear(2023);
        addedEntry.setNodelistName("nodelist.001");
        addedEntry.setChangeType(NodeHistoryEntry.ChangeType.ADDED);
        addedEntry.setNodeName("Test Node");
        addedEntry.setLocation("Test Location");
        addedEntry.setSysOpName("Test SysOp");
        addedEntry.setPhone("1234567890");
        addedEntry.setBaudRate(1200);
        addedEntry.setFlags(List.of("FLAG1", "FLAG2"));
        nodeHistoryEntryRepository.save(addedEntry);

        NodeHistoryEntry modifiedEntry = new NodeHistoryEntry();
        modifiedEntry.setZone(1);
        modifiedEntry.setNetwork(1);
        modifiedEntry.setNode(2);
        modifiedEntry.setChangeDate(LocalDate.of(2023, 1, 2));
        modifiedEntry.setNodelistYear(2023);
        modifiedEntry.setNodelistName("nodelist.002");
        modifiedEntry.setChangeType(NodeHistoryEntry.ChangeType.MODIFIED);
        modifiedEntry.setNodeName("Modified Node");
        modifiedEntry.setLocation("New Location");
        modifiedEntry.setPrevNodeName("Old Node");
        modifiedEntry.setPrevLocation("Old Location");
        nodeHistoryEntryRepository.save(modifiedEntry);
    }

    @AfterAll
    static void close() {
        postgreSQLContainer.close();
        minioContainer.close();
        kafkaContainer.close();
        redisContainer.close();
    }
}