package ru.oldzoomer.nodehistj_history_diff.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

/**
 * Service for managing node history and differences
 */
public interface NodeHistoryService {
    
    /**
     * Get history for a specific node
     */
    Page<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Get history for a specific network
     */
    Page<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable);

    /**
     * Get history for a specific zone
     */
    Page<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable);

    /**
     * Get all history entries
     */
    Page<NodeHistoryEntryDto> getAllHistory(Pageable pageable);

    /**
     * Get changes for a specific date
     */
    List<NodeHistoryEntryDto> getChangesForDate(LocalDate date);

    /**
     * Get changes between dates
     */
    Page<NodeHistoryEntryDto> getChangesBetweenDates(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Get changes by type
     */
    Page<NodeHistoryEntryDto> getChangesByType(NodeHistoryEntry.ChangeType changeType, Pageable pageable);

    /**
     * Get summary of changes for a date range
     */
    List<NodeChangeSummaryDto> getChangeSummary(LocalDate startDate, LocalDate endDate);

    /**
     * Get most active nodes (nodes with most changes)
     */
    List<Object[]> getMostActiveNodes(LocalDate startDate, LocalDate endDate, Pageable pageable);
}