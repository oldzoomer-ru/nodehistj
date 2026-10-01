package ru.oldzoomer.nodehistj_history_diff.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;
import ru.oldzoomer.nodehistj_history_diff.repo.NodelistEntryRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NodelistDiffProcessor.
 * Uses Mockito to mock repository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class NodelistDiffProcessorTest {

    @Mock
    private NodelistEntryRepository nodelistEntryRepository;

    @Mock
    private NodeHistoryEntryRepository nodeHistoryEntryRepository;

    @InjectMocks
    private NodelistDiffProcessor processor;

    @Test
    void processNodelistDiffs_lessThanTwoNodelists_shouldReturnEarly() {
        when(nodelistEntryRepository.count()).thenReturn(1L);

        processor.processNodelistDiffs();

        verify(nodelistEntryRepository).count();
        verify(nodeHistoryEntryRepository, never()).deleteAll();
        verify(nodelistEntryRepository, never()).findAllAsStreamWithSort();
    }

    @Test
    void processNodelistDiffs_noNodelists_shouldReturnEarly() {
        when(nodelistEntryRepository.count()).thenReturn(0L);

        processor.processNodelistDiffs();

        verify(nodelistEntryRepository).count();
        verify(nodeHistoryEntryRepository, never()).deleteAll();
    }

    @Test
    void processNodelistDiffs_withTwoNodelists_shouldProcessAndSave() {
        NodeEntry oldNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("Old Node").location("Old Location")
                .build();

        NodelistEntry oldNodelist = NodelistEntry.builder()
                .id(1L).nodelistYear(2024).dayOfYear(1)
                .nodeEntries(new HashSet<>(List.of(oldNode)))
                .build();

        NodeEntry newNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("New Node").location("New Location")
                .build();

        NodelistEntry newNodelist = NodelistEntry.builder()
                .id(2L).nodelistYear(2024).dayOfYear(2)
                .nodeEntries(new HashSet<>(List.of(newNode)))
                .build();

        when(nodelistEntryRepository.count()).thenReturn(2L);
        when(nodelistEntryRepository.findAllAsStreamWithSort())
                .thenReturn(Stream.of(oldNodelist, newNodelist));

        processor.processNodelistDiffs();

        verify(nodelistEntryRepository).count();
        verify(nodeHistoryEntryRepository).deleteAll();
        verify(nodelistEntryRepository).findAllAsStreamWithSort();
        verify(nodeHistoryEntryRepository, atLeastOnce()).save(any(NodeHistoryEntry.class));
    }

    @Test
    void processNodelistDiffs_emptyNodelists_shouldSaveHistoryEntries() {
        NodelistEntry emptyOld = NodelistEntry.builder()
                .id(1L).nodelistYear(2024).dayOfYear(1)
                .nodeEntries(new HashSet<>())
                .build();

        NodeEntry newNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("New Node").location("New Location")
                .build();

        NodelistEntry newWithNode = NodelistEntry.builder()
                .id(2L).nodelistYear(2024).dayOfYear(2)
                .nodeEntries(new HashSet<>(List.of(newNode)))
                .build();

        when(nodelistEntryRepository.count()).thenReturn(2L);
        when(nodelistEntryRepository.findAllAsStreamWithSort())
                .thenReturn(Stream.of(emptyOld, newWithNode));

        processor.processNodelistDiffs();

        ArgumentCaptor<NodeHistoryEntry> captor = ArgumentCaptor.forClass(NodeHistoryEntry.class);
        verify(nodeHistoryEntryRepository, atLeastOnce()).save(captor.capture());

        List<NodeHistoryEntry> savedEntries = captor.getAllValues();
        assertFalse(savedEntries.isEmpty());

        // Should have at least one ADDED entry
        boolean hasAdded = savedEntries.stream()
                .anyMatch(e -> e.getChangeType() == NodeHistoryEntry.ChangeType.ADDED);
        assertTrue(hasAdded, "Expected at least one ADDED entry");
    }

    @Test
    void processNodelistDiffs_removedNode_shouldSaveRemovedHistory() {
        NodeEntry oldNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("Old Node").location("Old Location")
                .build();

        NodelistEntry oldWithNode = NodelistEntry.builder()
                .id(1L).nodelistYear(2024).dayOfYear(1)
                .nodeEntries(new HashSet<>(List.of(oldNode)))
                .build();

        NodelistEntry emptyNew = NodelistEntry.builder()
                .id(2L).nodelistYear(2024).dayOfYear(2)
                .nodeEntries(new HashSet<>())
                .build();

        when(nodelistEntryRepository.count()).thenReturn(2L);
        when(nodelistEntryRepository.findAllAsStreamWithSort())
                .thenReturn(Stream.of(oldWithNode, emptyNew));

        processor.processNodelistDiffs();

        ArgumentCaptor<NodeHistoryEntry> captor = ArgumentCaptor.forClass(NodeHistoryEntry.class);
        verify(nodeHistoryEntryRepository, atLeastOnce()).save(captor.capture());

        List<NodeHistoryEntry> savedEntries = captor.getAllValues();

        // Should have at least one REMOVED entry
        boolean hasRemoved = savedEntries.stream()
                .anyMatch(e -> e.getChangeType() == NodeHistoryEntry.ChangeType.REMOVED);
        assertTrue(hasRemoved, "Expected at least one REMOVED entry");
    }

    @Test
    void processNodelistDiffs_identicalNodes_shouldNotSaveHistoryEntries() {
        NodeEntry identicalNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("Same Node").location("Same Location")
                .build();

        NodelistEntry oldSame = NodelistEntry.builder()
                .id(1L).nodelistYear(2024).dayOfYear(1)
                .nodeEntries(new HashSet<>(List.of(identicalNode)))
                .build();

        NodelistEntry newSame = NodelistEntry.builder()
                .id(2L).nodelistYear(2024).dayOfYear(2)
                .nodeEntries(new HashSet<>(List.of(identicalNode)))
                .build();

        when(nodelistEntryRepository.count()).thenReturn(2L);
        when(nodelistEntryRepository.findAllAsStreamWithSort())
                .thenReturn(Stream.of(oldSame, newSame));

        processor.processNodelistDiffs();

        // When nodes are identical, no history entries should be saved
        verify(nodeHistoryEntryRepository, never()).save(any(NodeHistoryEntry.class));
    }

    @Test
    void processNodelistDiffs_exceptionDuringProcessing_shouldNotThrow() {
        when(nodelistEntryRepository.count()).thenReturn(2L);
        when(nodelistEntryRepository.findAllAsStreamWithSort())
                .thenThrow(new RuntimeException("Database error"));

        // Should not throw - exception is caught internally
        assertDoesNotThrow(() -> processor.processNodelistDiffs());

        verify(nodelistEntryRepository).count();
    }

    @Test
    void processNodelistDiffs_modifiedNode_shouldSaveModifiedHistory() {
        NodeEntry oldNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("Old Name").location("Old Location")
                .build();

        NodelistEntry oldNodelist = NodelistEntry.builder()
                .id(1L).nodelistYear(2024).dayOfYear(1)
                .nodeEntries(new HashSet<>(List.of(oldNode)))
                .build();

        NodeEntry newNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("New Name").location("New Location")
                .build();

        NodelistEntry newNodelist = NodelistEntry.builder()
                .id(2L).nodelistYear(2024).dayOfYear(2)
                .nodeEntries(new HashSet<>(List.of(newNode)))
                .build();

        when(nodelistEntryRepository.count()).thenReturn(2L);
        when(nodelistEntryRepository.findAllAsStreamWithSort())
                .thenReturn(Stream.of(oldNodelist, newNodelist));

        processor.processNodelistDiffs();

        ArgumentCaptor<NodeHistoryEntry> captor = ArgumentCaptor.forClass(NodeHistoryEntry.class);
        verify(nodeHistoryEntryRepository, atLeastOnce()).save(captor.capture());

        List<NodeHistoryEntry> savedEntries = captor.getAllValues();

        // Should have at least one MODIFIED entry
        boolean hasModified = savedEntries.stream()
                .anyMatch(e -> e.getChangeType() == NodeHistoryEntry.ChangeType.MODIFIED);
        assertTrue(hasModified, "Expected at least one MODIFIED entry");
    }

    @Test
    void processNodelistDiffs_multipleNodesMixedChanges() {
        NodeEntry unchangedNode = NodeEntry.builder()
                .zone(1).network(1).node(3)
                .nodeName("Unchanged").location("Location")
                .build();

        NodeEntry oldNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("Old").location("Old Loc")
                .build();

        NodeEntry removedNode = NodeEntry.builder()
                .zone(1).network(1).node(4)
                .nodeName("Removed").build();

        NodelistEntry oldNodelist = NodelistEntry.builder()
                .id(1L).nodelistYear(2024).dayOfYear(1)
                .nodeEntries(new HashSet<>(List.of(oldNode, unchangedNode, removedNode)))
                .build();

        NodeEntry newNode = NodeEntry.builder()
                .zone(1).network(1).node(1)
                .nodeName("New").location("New Loc")
                .build();

        NodeEntry addedNode = NodeEntry.builder()
                .zone(1).network(1).node(5)
                .nodeName("Added").build();

        NodelistEntry newNodelist = NodelistEntry.builder()
                .id(2L).nodelistYear(2024).dayOfYear(2)
                .nodeEntries(new HashSet<>(List.of(newNode, unchangedNode, addedNode)))
                .build();

        when(nodelistEntryRepository.count()).thenReturn(2L);
        when(nodelistEntryRepository.findAllAsStreamWithSort())
                .thenReturn(Stream.of(oldNodelist, newNodelist));

        processor.processNodelistDiffs();

        ArgumentCaptor<NodeHistoryEntry> captor = ArgumentCaptor.forClass(NodeHistoryEntry.class);
        verify(nodeHistoryEntryRepository, atLeastOnce()).save(captor.capture());

        List<NodeHistoryEntry> savedEntries = captor.getAllValues();

        long addedCount = savedEntries.stream().filter(e -> e.getChangeType() == NodeHistoryEntry.ChangeType.ADDED).count();
        long removedCount = savedEntries.stream().filter(e -> e.getChangeType() == NodeHistoryEntry.ChangeType.REMOVED).count();
        long modifiedCount = savedEntries.stream().filter(e -> e.getChangeType() == NodeHistoryEntry.ChangeType.MODIFIED).count();

        assertEquals(1, addedCount, "Expected 1 ADDED entry");
        assertEquals(1, removedCount, "Expected 1 REMOVED entry");
        assertEquals(1, modifiedCount, "Expected 1 MODIFIED entry");
    }
}
