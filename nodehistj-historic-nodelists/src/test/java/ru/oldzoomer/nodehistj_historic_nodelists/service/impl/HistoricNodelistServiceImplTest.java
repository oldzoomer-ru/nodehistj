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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricNodelistServiceImplTest {

    @Mock
    private NodelistEntryRepository nodelistEntryRepository;

    @Mock
    private NodeEntryMapper nodeEntryMapper;

    @InjectMocks
    private HistoricNodelistServiceImpl historicNodelistService;

    private static final Year TEST_YEAR = Year.of(2023);
    private static final int TEST_DAY_OF_YEAR = 1;
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

        when(nodelistEntryRepository.existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(true);
        when(nodelistEntryRepository.findFirstByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntries(TEST_YEAR, TEST_DAY_OF_YEAR);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(nodelistEntryRepository).existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR);
        verify(nodelistEntryRepository).findFirstByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR);
    }

    @Test
    void testGetNodelistEntries_WithNoData_ReturnsEmptySet() {
        // Given
        when(nodelistEntryRepository.existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(false);

        // When
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntries(TEST_YEAR, TEST_DAY_OF_YEAR);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(nodelistEntryRepository).existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR);
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

        when(nodelistEntryRepository.existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(true);
        when(nodelistEntryRepository.findFirstByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE);

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

        when(nodelistEntryRepository.existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(true);
        when(nodelistEntryRepository.findFirstByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        Set<NodeEntryDto> result = historicNodelistService.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE, TEST_NETWORK);

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

        when(nodelistEntryRepository.existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(true);
        when(nodelistEntryRepository.findFirstByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(nodelistEntry);

        NodeEntryDto nodeEntryDto = new NodeEntryDto();
        when(nodeEntryMapper.toDto(any(NodeEntry.class))).thenReturn(nodeEntryDto);

        // When
        NodeEntryDto result = historicNodelistService.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE, TEST_NETWORK, TEST_NODE);

        // Then
        assertNotNull(result);
    }

    @Test
    void testGetNodelistEntry_WithNoMatchingEntry_ThrowsException() {
        // Given
        when(nodelistEntryRepository.existsByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(true);
        when(nodelistEntryRepository.findFirstByNodelistYearAndDayOfYear(TEST_YEAR.getValue(), TEST_DAY_OF_YEAR))
                .thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                historicNodelistService.getNodelistEntry(TEST_YEAR, TEST_DAY_OF_YEAR, TEST_ZONE, TEST_NETWORK, TEST_NODE));
    }
}