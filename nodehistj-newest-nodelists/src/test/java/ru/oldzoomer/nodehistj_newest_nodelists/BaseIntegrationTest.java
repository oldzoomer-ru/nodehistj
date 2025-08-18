package ru.oldzoomer.nodehistj_newest_nodelists;

import java.net.InetSocketAddress;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.redpanda.RedpandaContainer;
import org.testcontainers.utility.DockerImageName;

import com.datastax.oss.driver.api.core.CqlSession;
import com.redis.testcontainers.RedisContainer;

import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodeEntryRepository;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@Transactional
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    private static final String KEYSPACE_NAME = "nodehistj";

    @SuppressWarnings("resource")
    @Container
    public static final CassandraContainer cassandra = new CassandraContainer("cassandra")
            .withExposedPorts(9042)
            .withEnv("CASSANDRA_DC", "datacenter1")
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
    private NodeEntryRepository nodeEntryRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
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

    @BeforeAll
    static void createKeyspace() {
        if (cassandra.isRunning()) {
            try (CqlSession session = CqlSession.builder()
                    .addContactPoint(
                        InetSocketAddress.createUnresolved(cassandra.getHost(),
                        cassandra.getMappedPort(9042))
                    )
                    .withLocalDatacenter(cassandra.getLocalDatacenter())
                    .build()) {
                session.execute("DROP KEYSPACE IF EXISTS " + KEYSPACE_NAME + ";");
            }
        }
    }

    @BeforeEach
    void setUpDatabase() {
        nodeEntryRepository.deleteAll();

        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setNodelistName("nodelist.001");
        nodeEntry.setNodelistYear(2005);
        nodeEntry.setZone(1);
        nodeEntry.setNetwork(1);
        nodeEntry.setNode(1);
        nodeEntry.setNodeName("Test Node");
        nodeEntry.setLocation("Test Location");
        nodeEntry.setSysOpName("Test SysOp");
        nodeEntry.setPhone("1234567890");
        nodeEntry.setBaudRate(1200);
        nodeEntry.setFlags(List.of("FLAG1", "FLAG2"));
        nodeEntryRepository.insert(nodeEntry);
    }

    @AfterAll
    static void close() {
        cassandra.close();
        minioContainer.close();
        redpandaContainer.close();
        redisContainer.close();
    }
}
