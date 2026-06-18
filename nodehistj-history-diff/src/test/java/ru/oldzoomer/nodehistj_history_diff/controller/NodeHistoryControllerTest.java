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
        mockMvc.perform(get("/history?zone=1&network=1&node=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$.content[0].changeType").value("ADDED"));
    }

    @Test
    void testGetChangesByNetwork() throws Exception {
        mockMvc.perform(get("/history?zone=1&network=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").exists())
                .andExpect(jsonPath("$.content[0].changeType").exists());
    }

    @Test
    void testGetChangesByZone() throws Exception {
        mockMvc.perform(get("/history?zone=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").exists())
                .andExpect(jsonPath("$.content[0].changeType").exists());
    }

    @Test
    void testGetHistory_invalidZone() throws Exception {
        mockMvc.perform(get("/history?zone=99999"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistory_invalidTypeConversion() throws Exception {
        mockMvc.perform(get("/history?zone=abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistory_emptyResults() throws Exception {
        mockMvc.perform(get("/history?zone=99&network=99&node=99"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(0));
    }
}
