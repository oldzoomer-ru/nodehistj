package ru.oldzoomer.nodehistj_history_diff.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.oldzoomer.nodehistj_history_diff.BaseIntegrationTest;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for NodeHistoryServiceImpl.
 */
class NodeHistoryServiceImplIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NodeHistoryService nodeHistoryService;

    /**
     * Tests retrieval of all history entries.
     */
    @Test
    void testGetAllHistory() {
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getAllHistory(PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        NodeHistoryEntryDto entry = result.getContent().getFirst();
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
     * Tests retrieval of zone history.
     */
    @Test
    void testGetZoneHistory() {
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getZoneHistory(1, PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        NodeHistoryEntryDto entry = result.getContent().getFirst();
        assertEquals(1, entry.getZone());
    }

    /**
     * Tests retrieval of network history.
     */
    @Test
    void testGetNetworkHistory() {
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getNetworkHistory(1, 1, PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        NodeHistoryEntryDto entry = result.getContent().getFirst();
        assertEquals(1, entry.getZone());
        assertEquals(1, entry.getNetwork());
    }

    /**
     * Tests retrieval of node history.
     */
    @Test
    void testGetNodeHistory() {
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getNodeHistory(1, 1, 1, PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        NodeHistoryEntryDto entry = result.getContent().getFirst();
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
     * Tests retrieval of non-existent zone history.
     */
    @Test
    void testGetZoneHistoryWithNonExistentZone() {
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getZoneHistory(999, PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}