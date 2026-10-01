package ru.oldzoomer.nodehistj_history_diff.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.mapper.NodeHistoryEntryMapper;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NodeHistoryServiceImpl.
 * Uses Mockito to mock repository and mapper dependencies.
 */
@ExtendWith(MockitoExtension.class)
class NodeHistoryServiceImplTest {

    @Mock
    private NodeHistoryEntryRepository repository;

    @Mock
    private NodeHistoryEntryMapper mapper;

    @InjectMocks
    private NodeHistoryServiceImpl service;

    private static final Integer TEST_ZONE = 1;
    private static final Integer TEST_NETWORK = 1;
    private static final Integer TEST_NODE = 1;
    private static final Pageable TEST_PAGEABLE = PageRequest.of(0, 20);

    private NodeHistoryEntry testEntity;
    private NodeHistoryEntryDto testDto;

    @BeforeEach
    void setUp() {
        testEntity = NodeHistoryEntry.builder()
                .id(1L)
                .zone(TEST_ZONE)
                .network(TEST_NETWORK)
                .node(TEST_NODE)
                .changeDate(LocalDate.of(2024, 1, 1))
                .nodelistYear(2024)
                .dayOfYear(1)
                .changeType(NodeHistoryEntry.ChangeType.ADDED)
                .nodeName("Test Node")
                .location("Test Location")
                .build();

        testDto = new NodeHistoryEntryDto();
        testDto.setZone(TEST_ZONE);
        testDto.setNetwork(TEST_NETWORK);
        testDto.setNode(TEST_NODE);
        testDto.setNodeName("Test Node");
        testDto.setLocation("Test Location");
    }

    @Test
    void getNodeHistory_shouldReturnMappedPage() {
        Page<NodeHistoryEntry> entityPage = new PageImpl<>(List.of(testEntity));
        when(repository.findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE))
                .thenReturn(entityPage);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        Page<NodeHistoryEntryDto> result = service.getNodeHistory(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE);
        verify(mapper).toDto(testEntity);
    }

    @Test
    void getNodeHistory_emptyResult_shouldReturnEmptyPage() {
        Page<NodeHistoryEntry> entityPage = Page.empty();
        when(repository.findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE))
                .thenReturn(entityPage);

        Page<NodeHistoryEntryDto> result = service.getNodeHistory(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE);
    }

    @Test
    void getNetworkHistory_shouldReturnMappedPage() {
        Page<NodeHistoryEntry> entityPage = new PageImpl<>(List.of(testEntity));
        when(repository.findByZoneAndNetwork(TEST_ZONE, TEST_NETWORK, TEST_PAGEABLE))
                .thenReturn(entityPage);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        Page<NodeHistoryEntryDto> result = service.getNetworkHistory(TEST_ZONE, TEST_NETWORK, TEST_PAGEABLE);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByZoneAndNetwork(TEST_ZONE, TEST_NETWORK, TEST_PAGEABLE);
    }

    @Test
    void getZoneHistory_shouldReturnMappedPage() {
        Page<NodeHistoryEntry> entityPage = new PageImpl<>(List.of(testEntity));
        when(repository.findByZone(TEST_ZONE, TEST_PAGEABLE))
                .thenReturn(entityPage);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        Page<NodeHistoryEntryDto> result = service.getZoneHistory(TEST_ZONE, TEST_PAGEABLE);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByZone(TEST_ZONE, TEST_PAGEABLE);
    }

    @Test
    void getAllHistory_shouldReturnMappedPage() {
        Page<NodeHistoryEntry> entityPage = new PageImpl<>(List.of(testEntity));
        when(repository.findAll(TEST_PAGEABLE))
                .thenReturn(entityPage);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        Page<NodeHistoryEntryDto> result = service.getAllHistory(TEST_PAGEABLE);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(TEST_PAGEABLE);
    }

    @Test
    void getNodeHistory_multipleEntries_shouldReturnAllMapped() {
        NodeHistoryEntry entity2 = NodeHistoryEntry.builder()
                .id(2L)
                .zone(TEST_ZONE)
                .network(TEST_NETWORK)
                .node(TEST_NODE)
                .changeDate(LocalDate.of(2024, 1, 2))
                .changeType(NodeHistoryEntry.ChangeType.MODIFIED)
                .nodeName("Modified Node")
                .build();

        NodeHistoryEntryDto dto2 = new NodeHistoryEntryDto();
        dto2.setNodeName("Modified Node");

        Page<NodeHistoryEntry> entityPage = new PageImpl<>(List.of(testEntity, entity2));
        when(repository.findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE))
                .thenReturn(entityPage);
        when(mapper.toDto(testEntity)).thenReturn(testDto);
        when(mapper.toDto(entity2)).thenReturn(dto2);

        Page<NodeHistoryEntryDto> result = service.getNodeHistory(TEST_ZONE, TEST_NETWORK, TEST_NODE, TEST_PAGEABLE);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(mapper, times(2)).toDto(any(NodeHistoryEntry.class));
    }

    @Test
    void getNetworkHistory_emptyResult_shouldReturnEmptyPage() {
        when(repository.findByZoneAndNetwork(TEST_ZONE, TEST_NETWORK, TEST_PAGEABLE))
                .thenReturn(Page.empty());

        Page<NodeHistoryEntryDto> result = service.getNetworkHistory(TEST_ZONE, TEST_NETWORK, TEST_PAGEABLE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getZoneHistory_emptyResult_shouldReturnEmptyPage() {
        when(repository.findByZone(TEST_ZONE, TEST_PAGEABLE))
                .thenReturn(Page.empty());

        Page<NodeHistoryEntryDto> result = service.getZoneHistory(TEST_ZONE, TEST_PAGEABLE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllHistory_emptyResult_shouldReturnEmptyPage() {
        when(repository.findAll(TEST_PAGEABLE))
                .thenReturn(Page.empty());

        Page<NodeHistoryEntryDto> result = service.getAllHistory(TEST_PAGEABLE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNodeHistory_withCustomPageable_shouldPassPageableToRepository() {
        Pageable customPageable = PageRequest.of(2, 10);
        Page<NodeHistoryEntry> entityPage = Page.empty();
        when(repository.findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, customPageable))
                .thenReturn(entityPage);

        service.getNodeHistory(TEST_ZONE, TEST_NETWORK, TEST_NODE, customPageable);

        verify(repository).findByZoneAndNetworkAndNode(TEST_ZONE, TEST_NETWORK, TEST_NODE, customPageable);
    }
}
