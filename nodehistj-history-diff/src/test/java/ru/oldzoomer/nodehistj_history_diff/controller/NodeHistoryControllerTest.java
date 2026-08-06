package ru.oldzoomer.nodehistj_history_diff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

import ru.oldzoomer.nodehistj_history_diff.BaseIntegrationTest;

@Transactional
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
    void testGetAllHistory() throws Exception {
        mockMvc.perform(get("/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nodeName").exists())
                .andExpect(jsonPath("$.content[1].nodeName").exists());
    }

    @Test
    void testGetChangesByZone() throws Exception {
        mockMvc.perform(get("/history?zone=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").exists())
                .andExpect(jsonPath("$.content[0].changeType").exists());
    }

    @Test
    void testGetAllHistory_PaginationSizeOne_ShouldReturnOneEntry() throws Exception {
        mockMvc.perform(get("/history?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testGetAllHistory_SecondPage_ShouldReturnRemainingEntry() throws Exception {
        mockMvc.perform(get("/history?page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testGetChangesByNode_NonExistent_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/history?zone=99&network=99&node=999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void testGetChangesByNetwork_NonExistent_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/history?zone=99&network=999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void testGetChangesByZone_NonExistent_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/history?zone=99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void testGetChangesByNode_ModifiedEntry_HasPrevFields() throws Exception {
        mockMvc.perform(get("/history?zone=1&network=1&node=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].changeType").value("MODIFIED"))
                .andExpect(jsonPath("$.content[0].prevNodeName").value("Old Node"))
                .andExpect(jsonPath("$.content[0].prevLocation").value("Old Location"));
    }

    @Test
    void testGetAllHistory_PageBeyondData_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/history?page=10&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void testGetChangesByNode_ModifiedEntry_HasBothOldAndNewValues() throws Exception {
        mockMvc.perform(get("/history?zone=1&network=1&node=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nodeName").value("Modified Node"))
                .andExpect(jsonPath("$.content[0].prevNodeName").value("Old Node"))
                .andExpect(jsonPath("$.content[0].location").value("New Location"))
                .andExpect(jsonPath("$.content[0].prevLocation").value("Old Location"));
    }

    @Test
    void testGetAllHistory_PaginationMetadata_ShouldBePresent() throws Exception {
        mockMvc.perform(get("/history?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testGetAllHistory_SortOrderDesc_ShouldReturnNewestFirst() throws Exception {
        mockMvc.perform(get("/history?size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dayOfYear").value(2))
                .andExpect(jsonPath("$.content[1].dayOfYear").value(1));
    }
}