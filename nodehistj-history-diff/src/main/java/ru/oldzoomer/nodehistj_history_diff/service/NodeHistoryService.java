package ru.oldzoomer.nodehistj_history_diff.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

/**
 * Service for managing node history and differences
 */
public interface NodeHistoryService {
    
    /**
     * Get history for a specific node
     */
    Slice<Object> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Get history for a specific network
     */
    Slice<Object> getNetworkHistory(Integer zone, Integer network, Pageable pageable);

    /**
     * Get history for a specific zone
     */
    Slice<Object> getZoneHistory(Integer zone, Pageable pageable);

    /**
     * Get all history entries
     */
    Slice<Object> getAllHistory(Pageable pageable);

    /**
     * Get changes for a specific date
     */
    List<NodeHistoryEntryDto> getChangesForDate(LocalDate date);

    /**
     * Get changes between dates
     */
    Slice<Object> getChangesBetweenDates(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Get changes by type
     */
    Slice<Object> getChangesByType(NodeHistoryEntry.ChangeType changeType, Pageable pageable);
}