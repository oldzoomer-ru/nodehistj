package ru.oldzoomer.nodehistj_history_diff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.oldzoomer.nodehistj_history_diff.BaseIntegrationTest;

class NodeHistoryControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetChangesByNode() throws Exception {
        mockMvc.perform(get("/history?zone=1&network=1&node=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$[0].changeType").value("ADDED"));
    }

    @Test
    void testGetChangesByNetwork() throws Exception {
        mockMvc.perform(get("/history?zone=1&network=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").exists())
                .andExpect(jsonPath("$[0].changeType").exists());
    }

    @Test
    void testGetChangesByZone() throws Exception {
        mockMvc.perform(get("/history?zone=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").exists())
                .andExpect(jsonPath("$[0].changeType").exists());
    }
}