package ru.oldzoomer.nodehistj_history_diff.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

/**
 * Repository interface for NodeHistoryEntry entities.
 * Provides methods to find NodeHistoryEntry entities based on various criteria.
 */
public interface NodeHistoryEntryRepository extends JpaRepository<NodeHistoryEntry, Long> {

    /**
     * Gets history for a specific node.
     *
     * @param zone the zone of the node
     * @param network the network of the node
     * @param node the node number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities matching the criteria
     */
    Page<NodeHistoryEntry> findByZoneAndNetworkAndNodeOrderByChangeDateDesc(
            Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Gets history for a specific network.
     *
     * @param zone the zone of the network
     * @param network the network number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities matching the criteria
     */
    Page<NodeHistoryEntry> findByZoneAndNetworkOrderByChangeDateDescNodeAsc(
            Integer zone, Integer network, Pageable pageable);

    /**
     * Gets history for a specific zone.
     *
     * @param zone the zone number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities matching the criteria
     */
    Page<NodeHistoryEntry> findByZoneOrderByChangeDateDescNetworkAscNodeAsc(
            Integer zone, Pageable pageable);

    /**
     * Gets all history entries.
     *
     * @param pageable the pagination information
     * @return a page of all NodeHistoryEntry entities
     */
    Page<NodeHistoryEntry> findAllByOrderByChangeDateDescZoneAscNetworkAscNodeAsc(Pageable pageable);

    /**
     * Gets changes for a specific date.
     *
     * @param changeDate the date of the changes
     * @return a list of NodeHistoryEntry entities for the specified date
     */
    List<NodeHistoryEntry> findByChangeDateOrderByZoneAscNetworkAscNodeAsc(LocalDate changeDate);

    /**
     * Gets changes between dates.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities within the date range
     */
    Page<NodeHistoryEntry> findByChangeDateBetweenOrderByChangeDateDescZoneAscNetworkAscNodeAsc(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Gets changes by type.
     *
     * @param changeType the type of change
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities matching the change type
     */
    Page<NodeHistoryEntry> findByChangeTypeOrderByChangeDateDescZoneAscNetworkAscNodeAsc(
            NodeHistoryEntry.ChangeType changeType, Pageable pageable);

    /**
     * Gets summary statistics for a date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of NodeChangeSummaryDto objects with change statistics
     */
    @Query("""
            SELECT new ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto(
                h.changeDate, h.nodelistYear, h.nodelistName,
                    SUM(CASE WHEN h.changeType = 'ADDED' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN h.changeType = 'REMOVED' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN h.changeType = 'MODIFIED' THEN 1 ELSE 0 END),
                COUNT(h)
            )
            FROM NodeHistoryEntry h
            WHERE h.changeDate BETWEEN :startDate AND :endDate
            GROUP BY h.changeDate, h.nodelistYear, h.nodelistName
            ORDER BY h.changeDate DESC
            """)
    List<ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto> getChangeSummary(
            LocalDate startDate, LocalDate endDate);

    /**
     * Gets most active nodes (nodes with most changes) in a period.
     *
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @param pageable the pagination information
     * @return a list of Object arrays with node IDs and change counts
     */
    @Query("""
            SELECT h.zone, h.network, h.node, COUNT(h) as changeCount
            FROM NodeHistoryEntry h
            WHERE h.changeDate BETWEEN :startDate AND :endDate
            GROUP BY h.zone, h.network, h.node
            ORDER BY changeCount DESC
            """)
    List<Object[]> getMostActiveNodes(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Checks if a NodeHistoryEntry exists based on zone, network, node, nodelist year, and nodelist name.
     *
     * @param zone the zone of the node
     * @param network the network of the node
     * @param node the node number
     * @param nodelistYear the year of the nodelist
     * @param nodelistName the name of the nodelist
     * @return true if the NodeHistoryEntry exists, false otherwise
     */
    boolean existsByZoneAndNetworkAndNodeAndNodelistYearAndNodelistName(
            Integer zone, Integer network, Integer node, Integer nodelistYear, String nodelistName);
}