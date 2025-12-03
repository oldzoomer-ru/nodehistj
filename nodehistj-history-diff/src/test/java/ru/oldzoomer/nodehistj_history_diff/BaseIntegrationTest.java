package ru.oldzoomer.nodehistj_history_diff;

import com.redis.testcontainers.RedisContainer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.redpanda.RedpandaContainer;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@Transactional
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Container
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .waitingFor(Wait.forListeningPort());

    @Container
    public static final RedpandaContainer redpandaContainer = new RedpandaContainer("redpandadata/redpanda")
            .waitingFor(Wait.forSuccessfulCommand("rpk cluster health"));

    @Container
    public static final MinIOContainer minioContainer = new MinIOContainer("minio/minio")
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .waitingFor(Wait.forSuccessfulCommand("mc ready local"));

    @Container
    public static final RedisContainer redisContainer = new RedisContainer("redis:alpine")
            .waitingFor(Wait.forSuccessfulCommand("redis-cli ping"));

    @Autowired
    private NodeHistoryEntryRepository nodeHistoryEntryRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql:alpine:///testdb");
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.accessKey", minioContainer::getUserName);
        registry.add("minio.secretKey", minioContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", redpandaContainer::getBootstrapServers);
        registry.add("spring.data.redis.host", redisContainer::getRedisHost);
        registry.add("spring.data.redis.port", redisContainer::getRedisPort);
    }

    private static @NotNull NodeHistoryEntry getNodeHistoryEntry() {
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
        return addedEntry;
    }

    @BeforeEach
    void setUpDatabase() {
        nodeHistoryEntryRepository.deleteAll();

        // Create test history entries
        NodeHistoryEntry modifiedEntry = getHistoryEntry();
        nodeHistoryEntryRepository.save(modifiedEntry);
    }

    private @NotNull NodeHistoryEntry getHistoryEntry() {
        NodeHistoryEntry addedEntry = getNodeHistoryEntry();
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
        return modifiedEntry;
    }

    @AfterAll
    static void close() {
        minioContainer.close();
        redpandaContainer.close();
        redisContainer.close();
    }
}