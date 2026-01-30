package ru.oldzoomer.nodehistj_historic_nodelists.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oldzoomer.nodehistj_historic_nodelists.BaseIntegrationTest;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for HistoricNodelistServiceImpl.
 */
class HistoricNodelistServiceImplIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private HistoricNodelistService historicNodelistService;

    /**
     * Tests retrieval of historic nodelist entries without any filters.
     */
    @Test
    void testGetHistoricNodelistEntries() {
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntries(Year.of(2023), 1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        NodeEntryDto entry = result.iterator().next();
        assertEquals(1, entry.getZone());
        assertEquals(1, entry.getNetwork());
        assertEquals(1, entry.getNode());
        assertEquals("Test Node", entry.getNodeName());
        assertEquals("Test Location", entry.getLocation());
        assertEquals("Test SysOp", entry.getSysOpName());
        assertEquals("1234567890", entry.getPhone());
        assertEquals(1200, entry.getBaudRate());
        assertNotNull(entry.getFlags());
        assertEquals(2, entry.getFlags().size());
    }

    /**
     * Tests retrieval of historic nodelist entries with zone filter.
     */
    @Test
    void testGetHistoricNodelistEntriesWithZone() {
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntry(Year.of(2023), 1, 1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        NodeEntryDto entry = result.iterator().next();
        assertEquals(1, entry.getZone());
    }

    /**
     * Tests retrieval of historic nodelist entries with zone and network filter.
     */
    @Test
    void testGetHistoricNodelistEntriesWithZoneAndNetwork() {
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntry(Year.of(2023), 1, 1, 1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        NodeEntryDto entry = result.iterator().next();
        assertEquals(1, entry.getZone());
        assertEquals(1, entry.getNetwork());
    }

    /**
     * Tests retrieval of historic nodelist entries with zone, network and node filter.
     */
    @Test
    void testGetHistoricNodelistEntriesWithZoneNetworkAndNode() {
        NodeEntryDto result = historicNodelistService.getNodelistEntry(Year.of(2023), 1, 1, 1, 1);

        assertNotNull(result);
        assertEquals(1, result.getZone());
        assertEquals(1, result.getNetwork());
        assertEquals(1, result.getNode());
        assertEquals("Test Node", result.getNodeName());
        assertEquals("Test Location", result.getLocation());
        assertEquals("Test SysOp", result.getSysOpName());
        assertEquals("1234567890", result.getPhone());
        assertEquals(1200, result.getBaudRate());
        assertNotNull(result.getFlags());
        assertEquals(2, result.getFlags().size());
    }

    /**
     * Tests retrieval of historic nodelist entries with non-existent year.
     */
    @Test
    void testGetHistoricNodelistEntriesWithNonExistentYear() {
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntries(Year.of(2024), 1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}