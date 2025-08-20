package ru.oldzoomer.nodehistj_history_diff.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

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
}