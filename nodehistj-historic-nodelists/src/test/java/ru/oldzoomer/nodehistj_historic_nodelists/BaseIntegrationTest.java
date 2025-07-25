package ru.oldzoomer.nodehistj_historic_nodelists;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodelistEntryRepository;

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
            .withPassword("testpass");

    @Autowired
    private NodeEntryRepository nodeEntryRepository;

    @Autowired
    private NodelistEntryRepository nodelistEntryRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql:17-alpine:///testdb");
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUpDatabase() {
        nodeEntryRepository.deleteAll();
        nodelistEntryRepository.deleteAll();

        NodelistEntry nodelistEntry = new NodelistEntry();
        nodelistEntry.setNodelistYear(2023);
        nodelistEntry.setNodelistName("nodelist.001");
        nodelistEntryRepository.save(nodelistEntry);

        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setNodelistEntry(nodelistEntry);
        nodeEntry.setYear(2023);
        nodeEntry.setDayOfYear(1);
        nodeEntry.setZone(1);
        nodeEntry.setNetwork(1);
        nodeEntry.setNode(1);
        nodeEntry.setNodeName("Test Node");
        nodeEntry.setLocation("Test Location");
        nodeEntry.setSysOpName("Test SysOp");
        nodeEntry.setPhone("1234567890");
        nodeEntry.setBaudRate(1200);
        nodeEntry.setFlags(List.of("FLAG1", "FLAG2"));
        nodeEntryRepository.save(nodeEntry);
    }
}
