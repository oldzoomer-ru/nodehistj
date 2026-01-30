package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodelistServiceImplTest {

    @Mock
    private NodelistEntryRepository nodelistEntryRepository;

    @Mock
    private NodeEntryMapper nodeEntryMapper;

    @InjectMocks
    private NodelistServiceImpl nodelistService;

    private static final Integer TEST_ZONE = 1;
    private static final Integer TEST_NETWORK = 1;
    private static final Integer TEST_NODE = 1;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(nodelistEntryRepository, nodeEntryMapper);
    }

    @Test
    void testGetNodelistEntries_WithValidData_ReturnsExpectedSet() {
        // Given
        NodelistEntry nodelistEntry = new NodelistEntry();
        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setZone(TEST_ZONE);
        nodeEntry.setNetwork(TEST_NETWORK);
        nodeEntry.setNode(TEST_NODE);
        Set<NodeEntry> nodeEntries = Set.of(nodeEntry);
        nodelistEntry.setNodeEntries(nodeEntries);

        when(nodelistEntryRepository.findFirstBy()).thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        Set<NodeEntryDto> result = nodelistService.getNodelistEntries();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(nodelistEntryRepository).findFirstBy();
    }

    @Test
    void testGetNodelistEntries_WithNoData_ReturnsEmptySet() {
        // Given
        when(nodelistEntryRepository.findFirstBy()).thenReturn(null);

        // When
        Set<NodeEntryDto> result = nodelistService.getNodelistEntries();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(nodelistEntryRepository).findFirstBy();
    }

    @Test
    void testGetNodelistEntry_WithZone_ReturnsExpectedSet() {
        // Given
        NodelistEntry nodelistEntry = new NodelistEntry();
        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setZone(TEST_ZONE);
        nodeEntry.setNetwork(TEST_NETWORK);
        nodeEntry.setNode(TEST_NODE);
        Set<NodeEntry> nodeEntries = Set.of(nodeEntry);
        nodelistEntry.setNodeEntries(nodeEntries);

        when(nodelistEntryRepository.findFirstBy()).thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        Set<NodeEntryDto> result = nodelistService.getNodelistEntry(TEST_ZONE);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodelistEntry_WithNetwork_ReturnsExpectedSet() {
        // Given
        NodelistEntry nodelistEntry = new NodelistEntry();
        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setZone(TEST_ZONE);
        nodeEntry.setNetwork(TEST_NETWORK);
        nodeEntry.setNode(TEST_NODE);
        Set<NodeEntry> nodeEntries = Set.of(nodeEntry);
        nodelistEntry.setNodeEntries(nodeEntries);

        when(nodelistEntryRepository.findFirstBy()).thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        Set<NodeEntryDto> result = nodelistService.getNodelistEntry(TEST_ZONE, TEST_NETWORK);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodelistEntry_WithNode_ReturnsExpectedDto() {
        // Given
        NodelistEntry nodelistEntry = new NodelistEntry();
        NodeEntry nodeEntry = new NodeEntry();
        nodeEntry.setZone(TEST_ZONE);
        nodeEntry.setNetwork(TEST_NETWORK);
        nodeEntry.setNode(TEST_NODE);
        Set<NodeEntry> nodeEntries = Set.of(nodeEntry);
        nodelistEntry.setNodeEntries(nodeEntries);

        when(nodelistEntryRepository.findFirstBy()).thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        NodeEntryDto result = nodelistService.getNodelistEntry(TEST_ZONE, TEST_NETWORK, TEST_NODE);

        // Then
        assertNotNull(result);
    }

    @Test
    void testGetNodelistEntry_WithNoMatchingEntry_ThrowsException() {
        // Given
        when(nodelistEntryRepository.findFirstBy()).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                nodelistService.getNodelistEntry(TEST_ZONE, TEST_NETWORK, TEST_NODE));
    }
}