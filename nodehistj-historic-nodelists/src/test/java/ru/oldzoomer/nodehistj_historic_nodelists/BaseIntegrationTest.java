package ru.oldzoomer.nodehistj_historic_nodelists;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.redpanda.RedpandaContainer;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodelistEntryRepository;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection // Автоматически настраивает DataSource и Liquibase для Postgres
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .waitingFor(Wait.forListeningPort());

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection // Автоматически настраивает spring.kafka.bootstrap-servers
    public static final RedpandaContainer redpandaContainer = new RedpandaContainer("redpandadata/redpanda")
            .waitingFor(Wait.forSuccessfulCommand("rpk cluster health"));

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection // Автоматически настраивает пулы Redis (Jedis/Lettuce)
    public static final RedisContainer redisContainer = new RedisContainer("redis:alpine")
            .waitingFor(Wait.forSuccessfulCommand("redis-cli ping"));

    @SuppressWarnings("resource")
    @Container // Специфичный контейнер, настраивается вручную ниже
    public static final MinIOContainer minioContainer = new MinIOContainer("minio/minio")
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .waitingFor(Wait.forSuccessfulCommand("mc ready local"));

    @Autowired
    private NodelistEntryRepository nodelistEntryRepository;

    @DynamicPropertySource // Оставляем только для MinIO, так как для него нет @ServiceConnection
    static void registerMinioProperties(DynamicPropertyRegistry registry) {
        registry.add("s3.url", minioContainer::getS3URL);
        registry.add("s3.accessKey", minioContainer::getUserName);
        registry.add("s3.secretKey", minioContainer::getPassword);
    }

    @BeforeEach
    void setUpDatabase() {
        nodelistEntryRepository.deleteAll();

        NodelistEntry.NodelistEntryBuilder nodelistEntryBuild = NodelistEntry.builder();
        nodelistEntryBuild.nodelistYear(2023);
        nodelistEntryBuild.dayOfYear(1);

        NodeEntry.NodeEntryBuilder nodeEntryBuild = NodeEntry.builder();
        nodeEntryBuild.zone(1);
        nodeEntryBuild.network(1);
        nodeEntryBuild.node(1);
        nodeEntryBuild.nodeName("Test Node");
        nodeEntryBuild.location("Test Location");
        nodeEntryBuild.sysOpName("Test SysOp");
        nodeEntryBuild.phone("1234567890");
        nodeEntryBuild.baudRate(1200);
        nodeEntryBuild.flags(List.of("FLAG1", "FLAG2"));

        NodelistEntry nodelistEntry = nodelistEntryBuild.build();
        NodeEntry nodeEntry = nodeEntryBuild.build();

        nodelistEntry.getNodeEntries().add(nodeEntry);
        nodelistEntryRepository.save(nodelistEntry);
    }
}
