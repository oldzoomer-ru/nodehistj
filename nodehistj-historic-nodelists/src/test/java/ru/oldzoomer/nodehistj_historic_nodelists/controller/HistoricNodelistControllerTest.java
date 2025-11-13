package ru.oldzoomer.nodehistj_historic_nodelists.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import ru.oldzoomer.nodehistj_historic_nodelists.BaseIntegrationTest;

/**
 * Integration tests for HistoricNodelistController.
 * <p>
 * Tests various scenarios for retrieving historic nodelist entries:
 * - Basic retrieval without filters
 * - Filtering by zone
 * - Filtering by zone and network
 * - Filtering by zone, network, and node
 * <p>
 * Uses MockMvc to perform HTTP requests and verify responses.
 * <p>
 * This test class verifies that the HistoricNodelistController correctly handles
 * different types of requests and returns the expected responses.
 */
public class HistoricNodelistControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests basic retrieval of historic nodelist entries without any filters.
     *
     * @throws Exception if the test fails
     */
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

    /**
     * Tests retrieval of historic nodelist entries filtered by zone.
     *
     * @throws Exception if the test fails
     */
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

    /**
     * Tests retrieval of historic nodelist entries filtered by zone and network.
     *
     * @throws Exception if the test fails
     */
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

    /**
     * Tests retrieval of historic nodelist entries filtered by zone, network, and node.
     *
     * @throws Exception if the test fails
     */
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
}
