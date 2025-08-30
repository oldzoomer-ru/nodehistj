package ru.oldzoomer.nodehistj_history_diff.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

/**
 * Service for managing node history and differences.
 * Provides methods for retrieving node history, network history, zone history, and other related operations.
 */
public interface NodeHistoryService {

    /**
     * Gets history for a specific node.
     *
     * @param zone the zone of the node
     * @param network the network of the node
     * @param node the node number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with node history
     */
    Page<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Gets history for a specific network.
     *
     * @param zone the zone of the network
     * @param network the network number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with network history
     */
    Page<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable);

    /**
     * Gets history for a specific zone.
     *
     * @param zone the zone number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with zone history
     */
    Page<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable);

    /**
     * Gets all history entries.
     *
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects with all history entries
     */
    Page<NodeHistoryEntryDto> getAllHistory(Pageable pageable);

    /**
     * Gets changes for a specific date.
     *
     * @param date the date of the changes
     * @return a list of NodeHistoryEntryDto objects for the specified date
     */
    List<NodeHistoryEntryDto> getChangesForDate(LocalDate date);

    /**
     * Gets changes between dates.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects within the date range
     */
    Page<NodeHistoryEntryDto> getChangesBetweenDates(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Gets changes by type.
     *
     * @param changeType the type of change
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntryDto objects matching the change type
     */
    Page<NodeHistoryEntryDto> getChangesByType(NodeHistoryEntry.ChangeType changeType, Pageable pageable);

    /**
     * Gets summary of changes for a date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of NodeChangeSummaryDto objects with change statistics
     */
    List<NodeChangeSummaryDto> getChangeSummary(LocalDate startDate, LocalDate endDate);

    /**
     * Gets most active nodes (nodes with most changes) in a period.
     *
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @param pageable the pagination information
     * @return a list of Object arrays with node IDs and change counts
     */
    List<Object[]> getMostActiveNodes(LocalDate startDate, LocalDate endDate, Pageable pageable);
}