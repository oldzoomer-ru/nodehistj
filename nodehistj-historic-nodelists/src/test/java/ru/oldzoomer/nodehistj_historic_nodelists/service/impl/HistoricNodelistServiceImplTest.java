package ru.oldzoomer.nodehistj_historic_nodelists.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodelistEntryRepository;

import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HistoricNodelistServiceImpl.
 * Uses Mockito to mock repository and mapper dependencies.
 */
@ExtendWith(MockitoExtension.class)
class HistoricNodelistServiceImplTest {

    @Mock
    private NodelistEntryRepository repository;

    @Mock
    private NodeEntryMapper mapper;

    @InjectMocks
    private HistoricNodelistServiceImpl service;

    private static final Year TEST_YEAR = Year.of(2024);
    private static final Integer TEST_DAY_OF_YEAR = 1;
    private static final Integer TEST_ZONE = 1;
    private static final Integer TEST_NETWORK = 1;
    private static final Integer TEST_NODE = 1;

    private NodelistEntry testNodelistEntry;
    private NodeEntry testNodeEntry;
    private NodeEntryDto testDto;

    @BeforeEach
    void setUp() {
        testNodeEntry = NodeEntry.builder()
                .id(1L)
                .zone(TEST_ZONE)
                .network(TEST_NETWORK)
                .node(TEST_NODE)
                .nodeName("Test Node")
                .location("Test Location")
                .sysOpName("Test SysOp")
                .phone("1234567890")
                .baudRate(1200)
                .flags(Arrays.asList("FLAG1", "FLAG2"))
                .build();

        testNodelistEntry = NodelistEntry.builder()
                .id(1L)
                .nodelistYear(2024)
                .dayOfYear(1)
                .nodeEntries(new HashSet<>(List.of(testNodeEntry)))
                .build();

        testDto = new NodeEntryDto();
        testDto.setZone(TEST_ZONE);
        testDto.setNetwork(TEST_NETWORK);
        testDto.setNode(TEST_NODE);
        testDto.setNodeName("Test Node");
        testDto.setLocation("Test Location");
    }

    @Test
    void getNodelistEntries_shouldReturnAllEntries() {
        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(testNodelistEntry);
        when(mapper.toDto(testNodeEntry)).thenReturn(testDto);

        Set<NodeEntryDto> result = service.getNodelistEntries(TEST_YEAR, TEST_DAY_OF_YEAR);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).existsByNodelistYearAndDayOfYear(2024, 1);
        verify(repository).findFirstByNodelistYearAndDayOfYear(2024, 1);
    }

    @Test
    void getNodelistEntries_nonExistentYear_shouldReturnEmptySet() {
        when(repository.existsByNodelistYearAndDayOfYear(2025, 1)).thenReturn(false);

        Set<NodeEntryDto> result = service.getNodelistEntries(Year.of(2025), TEST_DAY_OF_YEAR);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).existsByNodelistYearAndDayOfYear(2025, 1);
        verify(repository, never()).findFirstByNodelistYearAndDayOfYear(anyInt(), anyInt());
    }

    @Test
    void getNodelistEntries_nullNodelistEntry_shouldReturnEmptySet() {
        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(null);

        Set<NodeEntryDto> result = service.getNodelistEntries(TEST_YEAR, TEST_DAY_OF_YEAR);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNodelistEntry_byZone_shouldFilterByZone() {
        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(testNodelistEntry);
        when(mapper.toDto(testNodeEntry)).thenReturn(testDto);

        Set<NodeEntryDto> result = service.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).existsByNodelistYearAndDayOfYear(2024, 1);
    }

    @Test
    void getNodelistEntry_byZoneAndNetwork_shouldFilterByBoth() {
        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(testNodelistEntry);
        when(mapper.toDto(testNodeEntry)).thenReturn(testDto);

        Set<NodeEntryDto> result = service.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE, TEST_NETWORK);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getNodelistEntry_byZoneNetworkAndNode_shouldReturnSingleEntry() {
        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(testNodelistEntry);
        when(mapper.toDto(testNodeEntry)).thenReturn(testDto);

        NodeEntryDto result = service.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE, TEST_NETWORK, TEST_NODE);

        assertNotNull(result);
        assertEquals(TEST_ZONE, result.getZone());
        assertEquals(TEST_NODE, result.getNode());
    }

    @Test
    void getNodelistEntry_byZoneNetworkAndNode_notFound_shouldThrowException() {
        NodeEntry differentNode = NodeEntry.builder()
                .id(2L)
                .zone(TEST_ZONE)
                .network(TEST_NETWORK)
                .node(999)
                .nodeName("Other Node")
                .build();

        NodelistEntry nodelistWithDifferentNode = NodelistEntry.builder()
                .id(1L)
                .nodelistYear(2024)
                .dayOfYear(1)
                .nodeEntries(new HashSet<>(List.of(differentNode)))
                .build();

        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(nodelistWithDifferentNode);

        assertThrows(IllegalArgumentException.class, () ->
                service.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE, TEST_NETWORK, TEST_NODE));
    }

    @Test
    void getNodelistEntries_multipleNodes_shouldReturnAll() {
        NodeEntry node2 = NodeEntry.builder()
                .id(2L)
                .zone(TEST_ZONE)
                .network(TEST_NETWORK)
                .node(2)
                .nodeName("Node 2")
                .location("Location 2")
                .build();

        NodeEntryDto dto2 = new NodeEntryDto();
        dto2.setNodeName("Node 2");

        Set<NodeEntry> nodeEntries = new HashSet<>(List.of(testNodeEntry, node2));
        NodelistEntry nodelistWithMultipleNodes = NodelistEntry.builder()
                .id(1L)
                .nodelistYear(2024)
                .dayOfYear(1)
                .nodeEntries(nodeEntries)
                .build();

        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(nodelistWithMultipleNodes);
        when(mapper.toDto(testNodeEntry)).thenReturn(testDto);
        when(mapper.toDto(node2)).thenReturn(dto2);

        Set<NodeEntryDto> result = service.getNodelistEntries(TEST_YEAR, TEST_DAY_OF_YEAR);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getNodelistEntry_byZone_onlyMatchingNodesReturned() {
        NodeEntry otherZoneNode = NodeEntry.builder()
                .id(2L)
                .zone(2)
                .network(1)
                .node(1)
                .nodeName("Zone 2 Node")
                .build();

        Set<NodeEntry> nodeEntries = new HashSet<>(List.of(testNodeEntry, otherZoneNode));
        NodelistEntry nodelist = NodelistEntry.builder()
                .id(1L)
                .nodelistYear(2024)
                .dayOfYear(1)
                .nodeEntries(nodeEntries)
                .build();

        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(nodelist);
        when(mapper.toDto(testNodeEntry)).thenReturn(testDto);

        Set<NodeEntryDto> result = service.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(dto -> TEST_ZONE.equals(dto.getZone())));
    }

    @Test
    void getNodelistEntry_byZoneAndNetwork_onlyMatchingNodesReturned() {
        NodeEntry otherNetworkNode = NodeEntry.builder()
                .id(2L)
                .zone(TEST_ZONE)
                .network(2)
                .node(1)
                .nodeName("Network 2 Node")
                .build();

        Set<NodeEntry> nodeEntries = new HashSet<>(List.of(testNodeEntry, otherNetworkNode));
        NodelistEntry nodelist = NodelistEntry.builder()
                .id(1L)
                .nodelistYear(2024)
                .dayOfYear(1)
                .nodeEntries(nodeEntries)
                .build();

        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(nodelist);
        when(mapper.toDto(testNodeEntry)).thenReturn(testDto);

        Set<NodeEntryDto> result = service.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE, TEST_NETWORK);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(dto -> TEST_NETWORK.equals(dto.getNetwork())));
    }

    @Test
    void getNodelistEntries_emptyNodeList_shouldReturnEmptySet() {
        NodelistEntry emptyNodelist = NodelistEntry.builder()
                .id(1L)
                .nodelistYear(2024)
                .dayOfYear(1)
                .nodeEntries(new HashSet<>())
                .build();

        when(repository.existsByNodelistYearAndDayOfYear(2024, 1)).thenReturn(true);
        when(repository.findFirstByNodelistYearAndDayOfYear(2024, 1)).thenReturn(emptyNodelist);

        Set<NodeEntryDto> result = service.getNodelistEntries(TEST_YEAR, TEST_DAY_OF_YEAR);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNodelistEntry_withDifferentYear_shouldQueryCorrectYear() {
        Year differentYear = Year.of(2023);
        when(repository.existsByNodelistYearAndDayOfYear(2023, 1)).thenReturn(false);

        Set<NodeEntryDto> result = service.getNodelistEntries(differentYear, TEST_DAY_OF_YEAR);

        assertTrue(result.isEmpty());
        verify(repository).existsByNodelistYearAndDayOfYear(2023, 1);
    }
}
