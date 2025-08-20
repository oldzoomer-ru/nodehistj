package ru.oldzoomer.nodehistj_history_diff;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.testcontainers.utility.DockerImageName;

import com.datastax.oss.driver.api.core.CqlSession;
import com.redis.testcontainers.RedisContainer;

import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@ActiveProfiles("test")
@Slf4j
public abstract class BaseIntegrationTest {
    private static final String KEYSPACE_NAME = "nodehistj";

    @SuppressWarnings("resource")
    @Container
    public static final CassandraContainer cassandra = new CassandraContainer("cassandra")
            .withExposedPorts(9042)
            .withEnv("CASSANDRA_DC", "datacenter1")
            .withEnv("CASSANDRA_ENDPOINT_SNITCH", "GossipingPropertyFileSnitch")
            .waitingFor(Wait.forListeningPort());

    @Container
    public static final RedpandaContainer redpandaContainer = new RedpandaContainer(
            DockerImageName.parse("redpandadata/redpanda"));

    @SuppressWarnings("resource")
    @Container
    public static final MinIOContainer minioContainer = new MinIOContainer(
            DockerImageName.parse("minio/minio"))
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .waitingFor(Wait.forListeningPort());

    @SuppressWarnings("resource")
    @Container
    public static final RedisContainer redisContainer = new RedisContainer(
            DockerImageName.parse("redis:alpine"))
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private NodeHistoryEntryRepository nodeHistoryEntryRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        log.info("Cassandra host: {}", cassandra.getHost());
        log.info("Cassandra port: {}", cassandra.getMappedPort(9042));
        log.info("Cassandra local datacenter: {}", cassandra.getLocalDatacenter());
        log.info("Cassandra keyspace name: {}", KEYSPACE_NAME);
        log.info("MinIO URL: {}", minioContainer.getS3URL());
        log.info("MinIO user: {}", minioContainer.getUserName());
        log.info("MinIO password: {}", minioContainer.getPassword());
        log.info("Redpanda bootstrap servers: {}", redpandaContainer.getBootstrapServers());
        log.info("Redis host: {}", redisContainer.getRedisHost());
        log.info("Redis port: {}", redisContainer.getRedisPort());
        log.info("Cassandra is running: {}", cassandra.isRunning());
        log.info("Cassandra container logs: {}", cassandra.getLogs());
        registry.add("spring.data.cassandra.contact-points", () -> cassandra.getHost());
        registry.add("spring.data.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.data.cassandra.local-datacenter", () -> cassandra.getLocalDatacenter());
        registry.add("spring.data.cassandra.keyspace-name", () -> KEYSPACE_NAME);
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.user", minioContainer::getUserName);
        registry.add("minio.password", minioContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", redpandaContainer::getBootstrapServers);
        registry.add("spring.data.redis.host", redisContainer::getRedisHost);
        registry.add("spring.data.redis.port", redisContainer::getRedisPort);
    }

    private static @NotNull NodeHistoryEntry getNodeHistoryEntry() {
        NodeHistoryEntry addedEntry = new NodeHistoryEntry();
        addedEntry.setId(UUID.randomUUID());
        addedEntry.setZone(1);
        addedEntry.setNetwork(1);
        addedEntry.setNode(1);
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

    @BeforeAll
    static void createKeyspace() {
        log.info("Waiting for Cassandra container to start...");
        int attempts = 0;
        while (!cassandra.isRunning() && attempts++ < 30) {
            try {
                Thread.sleep(1000);
                log.info("Waiting for Cassandra... Attempt {}", attempts);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!cassandra.isRunning()) {
            throw new IllegalStateException("Cassandra container failed to start");
        }
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(cassandra.getContactPoint())
                .withLocalDatacenter(cassandra.getLocalDatacenter())
                .build()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME +
                    " WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
        }
    }

    @BeforeEach
    void setUpDatabase() {
        nodeHistoryEntryRepository.deleteAll();

        // Create test history entries
        NodeHistoryEntry addedEntry = getNodeHistoryEntry();
        nodeHistoryEntryRepository.save(addedEntry);
        
        NodeHistoryEntry modifiedEntry = getHistoryEntry();
        nodeHistoryEntryRepository.save(modifiedEntry);
    }

    private @NotNull NodeHistoryEntry getHistoryEntry() {
        NodeHistoryEntry addedEntry = getNodeHistoryEntry();
        nodeHistoryEntryRepository.save(addedEntry);

        NodeHistoryEntry modifiedEntry = new NodeHistoryEntry();
        modifiedEntry.setId(UUID.randomUUID());
        modifiedEntry.setZone(1);
        modifiedEntry.setNetwork(1);
        modifiedEntry.setNode(2);
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
        cassandra.close();
        minioContainer.close();
        redpandaContainer.close();
        redisContainer.close();
    }
}