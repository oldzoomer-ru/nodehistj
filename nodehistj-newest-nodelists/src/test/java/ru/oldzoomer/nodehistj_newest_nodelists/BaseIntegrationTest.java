package ru.oldzoomer.nodehistj_newest_nodelists;

import com.redis.testcontainers.RedisContainer;
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
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
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
    private NodelistEntryRepository nodelistEntryRepository;

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

    @BeforeEach
    void setUpDatabase() {
        nodelistEntryRepository.deleteAll();

        NodelistEntry nodelistEntry = new NodelistEntry();
        nodelistEntry.setNodelistYear(2023);
        nodelistEntry.setDayOfYear(1);

        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setZone(1);
        nodeEntry.setNetwork(1);
        nodeEntry.setNode(1);
        nodeEntry.setNodeName("Test Node");
        nodeEntry.setLocation("Test Location");
        nodeEntry.setSysOpName("Test SysOp");
        nodeEntry.setPhone("1234567890");
        nodeEntry.setBaudRate(1200);
        nodeEntry.setFlags(List.of("FLAG1", "FLAG2"));

        nodelistEntry.getNodeEntries().add(nodeEntry);
        nodelistEntryRepository.save(nodelistEntry);
    }
}
