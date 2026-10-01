package ru.oldzoomer.nodehistj_history_diff.service;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;

/**
 * Service for managing node history and differences.
 * Provides methods for retrieving node history, network history, zone history, and other related operations.
 */
public interface NodeHistoryService {

    /**
     * Gets history for a specific node.
     *
     * @param zone     the zone of the node
     * @param network  the network of the node
     * @param node     the node number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with node history
     */
    Page<@NonNull NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Gets history for a specific network.
     *
     * @param zone     the zone of the network
     * @param network  the network number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with network history
     */
    Page<@NonNull NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable);

    /**
     * Gets history for a specific zone.
     *
     * @param zone     the zone number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with zone history
     */
    Page<@NonNull NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable);

    /**
     * Gets all history entries.
     *
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with all history entries
     */
    Page<@NonNull NodeHistoryEntryDto> getAllHistory(Pageable pageable);
}