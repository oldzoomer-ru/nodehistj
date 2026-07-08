package ru.oldzoomer.nodehistj_history_diff;

import com.redis.testcontainers.RedisContainer;
import org.jetbrains.annotations.NotNull;
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
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .waitingFor(Wait.forListeningPort());

    @SuppressWarnings("resource")
    @Container
    public static final RedpandaContainer redpandaContainer = new RedpandaContainer("redpandadata/redpanda")
            .waitingFor(Wait.forSuccessfulCommand("rpk cluster health"));

    @SuppressWarnings("resource")
    @Container
    public static final MinIOContainer minioContainer = new MinIOContainer("minio/minio")
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .waitingFor(Wait.forSuccessfulCommand("mc ready local"));

    @SuppressWarnings("resource")
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
        registry.add("s3.url", minioContainer::getS3URL);
        registry.add("s3.accessKey", minioContainer::getUserName);
        registry.add("s3.secretKey", minioContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", redpandaContainer::getBootstrapServers);
        registry.add("spring.data.redis.host", redisContainer::getRedisHost);
        registry.add("spring.data.redis.port", redisContainer::getRedisPort);
    }

    private static @NotNull NodeHistoryEntry getNodeHistoryEntry() {
        NodeHistoryEntry.NodeHistoryEntryBuilder addedEntry = NodeHistoryEntry.builder();
        addedEntry.zone(1);
        addedEntry.network(1);
        addedEntry.node(1);
        addedEntry.changeDate(LocalDate.of(2023, 1, 1));
        addedEntry.nodelistYear(2023);
        addedEntry.dayOfYear(1);
        addedEntry.changeType(NodeHistoryEntry.ChangeType.ADDED);
        addedEntry.nodeName("Test Node");
        addedEntry.location("Test Location");
        addedEntry.sysOpName("Test SysOp");
        addedEntry.phone("1234567890");
        addedEntry.baudRate(1200);
        addedEntry.flags(List.of("FLAG1", "FLAG2"));
        return addedEntry.build();
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

        NodeHistoryEntry.NodeHistoryEntryBuilder modifiedEntry = NodeHistoryEntry.builder();
        modifiedEntry.zone(1);
        modifiedEntry.network(1);
        modifiedEntry.node(2);
        modifiedEntry.changeDate(LocalDate.of(2023, 1, 2));
        modifiedEntry.nodelistYear(2023);
        modifiedEntry.dayOfYear(2);
        modifiedEntry.changeType(NodeHistoryEntry.ChangeType.MODIFIED);
        modifiedEntry.nodeName("Modified Node");
        modifiedEntry.location("New Location");
        modifiedEntry.prevNodeName("Old Node");
        modifiedEntry.prevLocation("Old Location");
        return modifiedEntry.build();
    }
}