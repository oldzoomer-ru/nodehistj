package ru.oldzoomer.nodehistj_historic_nodelists;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
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
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntryKey;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodeEntryRepository;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@Slf4j
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Container
    public static final CassandraContainer cassandra = new CassandraContainer("cassandra")
            .withExposedPorts(9042)
            .withEnv("CASSANDRA_DC", "datacenter1")
            .withEnv("CASSANDRA_ENDPOINT_SNITCH", "GossipingPropertyFileSnitch")
            .waitingFor(Wait.forSuccessfulCommand("cqlsh -e 'describe keyspaces;'"));

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
    private NodeEntryRepository nodeEntryRepository;

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

    @BeforeEach
    void setUpDatabase() {
        nodeEntryRepository.deleteAll();

        NodeEntryKey nodeEntryKey = new NodeEntryKey();
        nodeEntryKey.setZone(1);
        nodeEntryKey.setNetwork(1);
        nodeEntryKey.setNode(1);
        nodeEntryKey.setNodelistYear(2023);
        nodeEntryKey.setNodelistName("nodelist.001");

        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setId(nodeEntryKey);
        nodeEntry.setNodeName("Test Node");
        nodeEntry.setLocation("Test Location");
        nodeEntry.setSysOpName("Test SysOp");
        nodeEntry.setPhone("1234567890");
        nodeEntry.setBaudRate(1200);
        nodeEntry.setFlags(List.of("FLAG1", "FLAG2"));
        nodeEntryRepository.save(nodeEntry);
    }

    @AfterAll
    static void close() {
        cassandra.close();
        minioContainer.close();
        redpandaContainer.close();
        redisContainer.close();
    }
}
