package ru.oldzoomer.nodehistj_history_diff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.oldzoomer.nodehistj_history_diff.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NodeHistoryControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetChangesByNode() throws Exception {
        mockMvc.perform(get("/history/node/1/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$[0].changeType").value("ADDED"));
    }

    @Test
    void testGetChangesByType() throws Exception {
        mockMvc.perform(get("/history/type/MODIFIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Modified Node"))
                .andExpect(jsonPath("$[0].changeType").value("MODIFIED"))
                .andExpect(jsonPath("$[0].prevNodeName").value("Old Node"));
    }
}