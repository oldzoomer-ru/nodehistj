package ru.gavrilovegor519.nodehistj_history_diff.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.gavrilovegor519.nodehistj_history_diff.entity.NodelistEntry;
import ru.gavrilovegor519.nodehistj_history_diff.repo.NodeEntryRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class NodeHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NodeEntryRepository repository;

    @Test
    void testGetNodeHistory() throws Exception {
        NodelistEntry mockEntry = new NodelistEntry();
        mockEntry.setNodelistName("Test Name");
        when(repository.findById(any())).thenReturn(Optional.of(mockEntry));

        mockMvc.perform(get("/history/node/1/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$.content[0].changeType").value("ADDED"));
    }

    @Test
    void testGetChangesForDate() throws Exception {
        mockMvc.perform(get("/history/date/2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$[0].changeType").value("ADDED"));
    }

    @Test
    void testGetChangesByType() throws Exception {
        mockMvc.perform(get("/history/type/MODIFIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").value("Modified Node"))
                .andExpect(jsonPath("$.content[0].changeType").value("MODIFIED"))
                .andExpect(jsonPath("$.content[0].prevNodeName").value("Old Node"));
    }

    @Test
    void testGetChangeSummary() throws Exception {
        mockMvc.perform(get("/history/summary")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].addedCount").value(1))
                .andExpect(jsonPath("$[1].modifiedCount").value(1));
    }
}