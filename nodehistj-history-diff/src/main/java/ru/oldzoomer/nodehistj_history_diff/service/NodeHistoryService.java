package ru.oldzoomer.nodehistj_history_diff.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;

/**
 * Service for managing node history and differences
 */
public interface NodeHistoryService {
    
    /**
     * Get history for a specific node
     */
    List<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Get history for a specific network
     */
    List<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable);

    /**
     * Get history for a specific zone
     */
    List<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable);

    /**
     * Get all history entries
     */
    List<NodeHistoryEntryDto> getAllHistory(Pageable pageable);

    /**
     * Get changes by type
     */
    List<NodeHistoryEntryDto> getChangesByType(String changeType);
}