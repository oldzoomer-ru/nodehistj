package ru.oldzoomer.nodehistj_newest_nodelists.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.oldzoomer.nodehistj_newest_nodelists.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for NodelistController.
 */
class NodelistControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests basic retrieval of newest nodelist entries without any filters.
     *
     * @throws Exception if the test fails
     */
    @Test
    void testGetNodelistEntry() throws Exception {
        mockMvc.perform(get("/nodelist"))
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
     * Tests retrieval of newest nodelist entries filtered by zone.
     *
     * @throws Exception if the test fails
     */
    @Test
    void testGetNodelistEntryWithZone() throws Exception {
        mockMvc.perform(get("/nodelist")
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
     * Tests retrieval of newest nodelist entries filtered by zone and network.
     *
     * @throws Exception if the test fails
     */
    @Test
    void testGetNodelistEntryWithZoneAndNetwork() throws Exception {
        mockMvc.perform(get("/nodelist")
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
     * Tests retrieval of newest nodelist entries filtered by zone, network, and node.
     *
     * @throws Exception if the test fails
     */
    @Test
    void testGetNodelistEntryWithZoneNetworkAndNode() throws Exception {
        mockMvc.perform(get("/nodelist")
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

    /**
     * Tests retrieval of newest nodelist entries with non-existent zone.
     *
     * @throws Exception if the test fails
     */
    @Test
    void testGetNodelistEntryWithNonExistentZone() throws Exception {
        mockMvc.perform(get("/nodelist")
                        .param("zone", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}