package ru.gavrilovegor519.nodehistj_history_diff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.gavrilovegor519.nodehistj_history_diff.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NodeHistoryControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetNodeHistory() throws Exception {
        mockMvc.perform(get("/history/node/1/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$.content[0].changeType").value("ADDED"));
    }

    @Test
    void testGetChangesForDate() throws Exception {
        mockMvc.perform(get("/history/date/2023-01-01"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpected(jsonPath("$[0].changeType").value("ADDED"));
    }

    @Test
    void testGetChangesByType() throws Exception {
        mockMvc.perform(get("/history/type/MODIFIED"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.content[0].nodeName").value("Modified Node"))
                .andExpected(jsonPath("$.content[0].changeType").value("MODIFIED"))
                .andExpected(jsonPath("$.content[0].prevNodeName").value("Old Node"));
    }

    @Test
    void testGetChangeSummary() throws Exception {
        mockMvc.perform(get("/history/summary")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-02"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$[0].addedCount").value(1))
                .andExpected(jsonPath("$[1].modifiedCount").value(1));
    }
}