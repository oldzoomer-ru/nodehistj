package ru.oldzoomer.nodehistj_history_diff;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.nodehistj.test.BaseContainerTest;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIntegrationTest extends BaseContainerTest {

    @Autowired
    private NodeHistoryEntryRepository nodeHistoryEntryRepository;

    private static @NotNull NodeHistoryEntry createNodeHistoryEntry() {
        NodeHistoryEntry addedEntry = new NodeHistoryEntry();
        addedEntry.setZone(1);
        addedEntry.setNetwork(1);
        addedEntry.setNode(1);
        addedEntry.setChangeDate(LocalDate.of(2023, 1, 1));
        addedEntry.setNodelistYear(2023);
        addedEntry.setDayOfYear(1);
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

        NodeHistoryEntry modifiedEntry = setupAndGetHistoryEntry();
        nodeHistoryEntryRepository.save(modifiedEntry);
    }

    private @NotNull NodeHistoryEntry setupAndGetHistoryEntry() {
        NodeHistoryEntry addedEntry = createNodeHistoryEntry();
        nodeHistoryEntryRepository.save(addedEntry);

        NodeHistoryEntry modifiedEntry = new NodeHistoryEntry();
        modifiedEntry.setZone(1);
        modifiedEntry.setNetwork(1);
        modifiedEntry.setNode(2);
        modifiedEntry.setChangeDate(LocalDate.of(2023, 1, 2));
        modifiedEntry.setNodelistYear(2023);
        modifiedEntry.setDayOfYear(2);
        modifiedEntry.setChangeType(NodeHistoryEntry.ChangeType.MODIFIED);
        modifiedEntry.setNodeName("Modified Node");
        modifiedEntry.setLocation("New Location");
        modifiedEntry.setPrevNodeName("Old Node");
        modifiedEntry.setPrevLocation("Old Location");
        return modifiedEntry;
    }
}
