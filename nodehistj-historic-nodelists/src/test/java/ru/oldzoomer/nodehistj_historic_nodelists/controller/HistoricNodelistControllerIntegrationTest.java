package ru.oldzoomer.nodehistj_historic_nodelists.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import ru.oldzoomer.nodehistj_historic_nodelists.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for HistoricNodelistController.
 */
@Transactional
class HistoricNodelistControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetHistoricNodelistEntry() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023")
                        .param("dayOfYear", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$[0].location").value("Test Location"))
                .andExpect(jsonPath("$[0].sysOpName").value("Test SysOp"))
                .andExpect(jsonPath("$[0].phone").value("1234567890"))
                .andExpect(jsonPath("$[0].baudRate").value(1200))
                .andExpect(jsonPath("$[0].flags[0]").value("FLAG1"))
                .andExpect(jsonPath("$[0].flags[1]").value("FLAG2"));
    }

    @Test
    void testGetHistoricNodelistEntryWithZone() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023")
                        .param("dayOfYear", "1")
                        .param("zone", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$[0].location").value("Test Location"))
                .andExpect(jsonPath("$[0].sysOpName").value("Test SysOp"))
                .andExpect(jsonPath("$[0].phone").value("1234567890"))
                .andExpect(jsonPath("$[0].baudRate").value(1200))
                .andExpect(jsonPath("$[0].flags[0]").value("FLAG1"))
                .andExpect(jsonPath("$[0].flags[1]").value("FLAG2"));
    }

    @Test
    void testGetHistoricNodelistEntryWithZoneAndNetwork() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023")
                        .param("dayOfYear", "1")
                        .param("zone", "1")
                        .param("network", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$[0].location").value("Test Location"))
                .andExpect(jsonPath("$[0].sysOpName").value("Test SysOp"))
                .andExpect(jsonPath("$[0].phone").value("1234567890"))
                .andExpect(jsonPath("$[0].baudRate").value(1200))
                .andExpect(jsonPath("$[0].flags[0]").value("FLAG1"))
                .andExpect(jsonPath("$[0].flags[1]").value("FLAG2"));
    }

    @Test
    void testGetHistoricNodelistEntryWithZoneNetworkAndNode() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023")
                        .param("dayOfYear", "1")
                        .param("zone", "1")
                        .param("network", "1")
                        .param("node", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"))
                .andExpect(jsonPath("$[0].location").value("Test Location"))
                .andExpect(jsonPath("$[0].sysOpName").value("Test SysOp"))
                .andExpect(jsonPath("$[0].phone").value("1234567890"))
                .andExpect(jsonPath("$[0].baudRate").value(1200))
                .andExpect(jsonPath("$[0].flags[0]").value("FLAG1"))
                .andExpect(jsonPath("$[0].flags[1]").value("FLAG2"));
    }

    @Test
    void testGetHistoricNodelistEntryWithNonExistentYear() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2024")
                        .param("dayOfYear", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetHistoricNodelistEntry_MissingYear_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("dayOfYear", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistoricNodelistEntry_MissingDayOfYear_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistoricNodelistEntry_NoAddressFilter_ShouldReturnAllEntries() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023")
                        .param("dayOfYear", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nodeName").value("Test Node"));
    }

    @Test
    void testGetHistoricNodelistEntry_NonExistentZone_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023")
                        .param("dayOfYear", "1")
                        .param("zone", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetHistoricNodelistEntry_NonExistentDayOfYear_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/historicNodelist")
                        .param("year", "2023")
                        .param("dayOfYear", "365"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}