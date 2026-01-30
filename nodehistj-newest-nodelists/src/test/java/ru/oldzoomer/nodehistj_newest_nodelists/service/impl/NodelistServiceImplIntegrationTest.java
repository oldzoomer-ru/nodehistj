package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oldzoomer.nodehistj_newest_nodelists.BaseIntegrationTest;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for NodelistServiceImpl.
 */
class NodelistServiceImplIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NodelistService nodelistService;

    /**
     * Tests retrieval of newest nodelist entries without any filters.
     */
    @Test
    void testGetNodelistEntries() {
        Set<NodeEntryDto> result = nodelistService.getNodelistEntries();

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
     * Tests retrieval of newest nodelist entries with zone filter.
     */
    @Test
    void testGetNodelistEntriesWithZone() {
        Set<NodeEntryDto> result = nodelistService.getNodelistEntry(1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        NodeEntryDto entry = result.iterator().next();
        assertEquals(1, entry.getZone());
    }

    /**
     * Tests retrieval of newest nodelist entries with zone and network filter.
     */
    @Test
    void testGetNodelistEntriesWithZoneAndNetwork() {
        Set<NodeEntryDto> result = nodelistService.getNodelistEntry(1, 1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        NodeEntryDto entry = result.iterator().next();
        assertEquals(1, entry.getZone());
        assertEquals(1, entry.getNetwork());
    }

    /**
     * Tests retrieval of newest nodelist entries with zone, network and node filter.
     */
    @Test
    void testGetNodelistEntriesWithZoneNetworkAndNode() {
        NodeEntryDto result = nodelistService.getNodelistEntry(1, 1, 1);

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
     * Tests retrieval of newest nodelist entries with non-existent zone.
     */
    @Test
    void testGetNodelistEntriesWithNonExistentZone() {
        Set<NodeEntryDto> result = nodelistService.getNodelistEntry(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}