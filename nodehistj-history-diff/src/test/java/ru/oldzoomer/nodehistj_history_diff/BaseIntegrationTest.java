package ru.oldzoomer.nodehistj_history_diff;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.redpanda.RedpandaContainer;

import com.redis.testcontainers.RedisContainer;

import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntryKey;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@ActiveProfiles("test")
@Slf4j
public abstract class BaseIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    public static final CassandraContainer cassandra = new CassandraContainer("cassandra")
            .withExposedPorts(9042)
            .withEnv("CASSANDRA_DC", "datacenter1")
            .withEnv("CASSANDRA_ENDPOINT_SNITCH", "GossipingPropertyFileSnitch")
            .waitingFor(Wait.forSuccessfulCommand("cqlsh -e 'describe keyspaces;'"));

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
        registry.add("spring.cassandra.contact-points", () -> cassandra.getContactPoint().getHostName());
        registry.add("spring.cassandra.port", () -> cassandra.getContactPoint().getPort());
        registry.add("spring.cassandra.local-datacenter", cassandra::getLocalDatacenter);
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.user", minioContainer::getUserName);
        registry.add("minio.password", minioContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", redpandaContainer::getBootstrapServers);
        registry.add("spring.data.redis.host", redisContainer::getRedisHost);
        registry.add("spring.data.redis.port", redisContainer::getRedisPort);
    }

    private static @NotNull NodeHistoryEntry getNodeHistoryEntry() {
        NodeEntryKey nodeEntryKey = new NodeEntryKey();
        nodeEntryKey.setZone(1);
        nodeEntryKey.setNetwork(1);
        nodeEntryKey.setNode(1);
        nodeEntryKey.setNodelistYear(2013);
        nodeEntryKey.setNodelistName("nodelist.001");

        NodeHistoryEntry addedEntry = new NodeHistoryEntry();
        addedEntry.setId(nodeEntryKey);
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
        NodeHistoryEntry addedEntry = getNodeHistoryEntry();
        nodeHistoryEntryRepository.save(addedEntry);

        NodeHistoryEntry modifiedEntry = getHistoryEntry();
        nodeHistoryEntryRepository.save(modifiedEntry);

        // Create another entry for change summary test
        NodeEntryKey nodeEntryKey = new NodeEntryKey();
        nodeEntryKey.setZone(1);
        nodeEntryKey.setNetwork(1);
        nodeEntryKey.setNode(3);
        nodeEntryKey.setNodelistYear(2023);
        nodeEntryKey.setNodelistName("nodelist.003");

        NodeHistoryEntry removedEntry = new NodeHistoryEntry();
        removedEntry.setId(nodeEntryKey);
        removedEntry.setChangeType(NodeHistoryEntry.ChangeType.REMOVED);
        removedEntry.setNodeName("Removed Node");
        removedEntry.setLocation("Removed Location");
        removedEntry.setSysOpName("Test SysOp");
        removedEntry.setPhone("1234567890");
        removedEntry.setBaudRate(1200);
        removedEntry.setFlags(List.of("FLAG1", "FLAG2"));
        nodeHistoryEntryRepository.save(removedEntry);
    }

    private @NotNull NodeHistoryEntry getHistoryEntry() {
        NodeHistoryEntry addedEntry = getNodeHistoryEntry();
        nodeHistoryEntryRepository.save(addedEntry);

        NodeHistoryEntry modifiedEntry = new NodeHistoryEntry();
        NodeEntryKey nodeEntryKey = new NodeEntryKey();
        nodeEntryKey.setZone(1);
        nodeEntryKey.setNetwork(1);
        nodeEntryKey.setNode(2);
        nodeEntryKey.setNodelistYear(2023);
        nodeEntryKey.setNodelistName("nodelist.002");
        
        modifiedEntry.setId(nodeEntryKey);
        modifiedEntry.setChangeType(NodeHistoryEntry.ChangeType.MODIFIED);
        modifiedEntry.setNodeName("Modified Node");
        modifiedEntry.setLocation("New Location");
        modifiedEntry.setPrevNodeName("Old Node");
        modifiedEntry.setPrevLocation("Old Location");
        return modifiedEntry;
    }

    @AfterAll
    static void close() {
        cassandra.close();
        minioContainer.close();
        redpandaContainer.close();
        redisContainer.close();
    }
}