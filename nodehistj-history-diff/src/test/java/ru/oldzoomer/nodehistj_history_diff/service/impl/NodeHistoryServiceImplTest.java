package ru.oldzoomer.nodehistj_history_diff.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.mapper.NodeHistoryEntryMapper;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeHistoryServiceImplTest {

    @Mock
    private NodeHistoryEntryRepository nodeHistoryEntryRepository;

    @Mock
    private NodeHistoryEntryMapper nodeHistoryEntryMapper;

    @InjectMocks
    private NodeHistoryServiceImpl nodeHistoryService;

    private static final Integer TEST_ZONE = 1;
    private static final Integer TEST_NETWORK = 1;
    private static final Integer TEST_NODE = 1;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(nodeHistoryEntryRepository, nodeHistoryEntryMapper);
    }

    @Test
    void testGetNodeHistory_WithValidData_ReturnsExpectedPage() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        NodeHistoryEntry historyEntry = new NodeHistoryEntry();
        Page<NodeHistoryEntry> page = new PageImpl<>(List.of(historyEntry));

        when(nodeHistoryEntryRepository.findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, pageable))
                .thenReturn(page);

        NodeHistoryEntryDto dto = new NodeHistoryEntryDto();
        when(nodeHistoryEntryMapper.toDto(any(NodeHistoryEntry.class))).thenReturn(dto);

        // When
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getNodeHistory(TEST_ZONE, TEST_NETWORK, TEST_NODE, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(nodeHistoryEntryRepository).findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, pageable);
    }

    @Test
    void testGetNetworkHistory_WithValidData_ReturnsExpectedPage() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        NodeHistoryEntry historyEntry = new NodeHistoryEntry();
        Page<NodeHistoryEntry> page = new PageImpl<>(List.of(historyEntry));

        when(nodeHistoryEntryRepository.findByZoneAndNetwork(TEST_ZONE, TEST_NETWORK, pageable))
                .thenReturn(page);

        NodeHistoryEntryDto dto = new NodeHistoryEntryDto();
        when(nodeHistoryEntryMapper.toDto(any(NodeHistoryEntry.class))).thenReturn(dto);

        // When
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getNetworkHistory(TEST_ZONE, TEST_NETWORK, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(nodeHistoryEntryRepository).findByZoneAndNetwork(TEST_ZONE, TEST_NETWORK, pageable);
    }

    @Test
    void testGetZoneHistory_WithValidData_ReturnsExpectedPage() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        NodeHistoryEntry historyEntry = new NodeHistoryEntry();
        Page<NodeHistoryEntry> page = new PageImpl<>(List.of(historyEntry));

        when(nodeHistoryEntryRepository.findByZone(TEST_ZONE, pageable))
                .thenReturn(page);

        NodeHistoryEntryDto dto = new NodeHistoryEntryDto();
        when(nodeHistoryEntryMapper.toDto(any(NodeHistoryEntry.class))).thenReturn(dto);

        // When
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getZoneHistory(TEST_ZONE, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(nodeHistoryEntryRepository).findByZone(TEST_ZONE, pageable);
    }

    @Test
    void testGetAllHistory_WithValidData_ReturnsExpectedPage() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        NodeHistoryEntry historyEntry = new NodeHistoryEntry();
        Page<NodeHistoryEntry> page = new PageImpl<>(List.of(historyEntry));

        when(nodeHistoryEntryRepository.findAll(pageable))
                .thenReturn(page);

        NodeHistoryEntryDto dto = new NodeHistoryEntryDto();
        when(nodeHistoryEntryMapper.toDto(any(NodeHistoryEntry.class))).thenReturn(dto);

        // When
        Page<NodeHistoryEntryDto> result = nodeHistoryService.getAllHistory(pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(nodeHistoryEntryRepository).findAll(pageable);
    }
}