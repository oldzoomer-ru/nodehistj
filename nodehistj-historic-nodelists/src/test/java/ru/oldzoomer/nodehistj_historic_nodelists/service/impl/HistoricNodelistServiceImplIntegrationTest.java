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
        assertEquals(1, entry.zone());
        assertEquals(1, entry.network());
        assertEquals(1, entry.node());
        assertEquals("Test Node", entry.nodeName());
        assertEquals("Test Location", entry.location());
        assertEquals("Test SysOp", entry.sysOpName());
        assertEquals("1234567890", entry.phone());
        assertEquals(1200, entry.baudRate());
        assertNotNull(entry.flags());
        assertEquals(2, entry.flags().size());
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
        assertEquals(1, entry.zone());
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
        assertEquals(1, entry.zone());
        assertEquals(1, entry.network());
    }

    /**
     * Tests retrieval of historic nodelist entries with zone, network and node filter.
     */
    @Test
    void testGetHistoricNodelistEntriesWithZoneNetworkAndNode() {
        NodeEntryDto result = historicNodelistService.getNodelistEntry(Year.of(2023), 1, 1, 1, 1);

        assertNotNull(result);
        assertEquals(1, result.zone());
        assertEquals(1, result.network());
        assertEquals(1, result.node());
        assertEquals("Test Node", result.nodeName());
        assertEquals("Test Location", result.location());
        assertEquals("Test SysOp", result.sysOpName());
        assertEquals("1234567890", result.phone());
        assertEquals(1200, result.baudRate());
        assertNotNull(result.flags());
        assertEquals(2, result.flags().size());
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