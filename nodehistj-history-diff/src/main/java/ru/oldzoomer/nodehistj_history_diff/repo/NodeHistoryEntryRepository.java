package ru.oldzoomer.nodehistj_history_diff.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

import java.time.LocalDate;
import java.util.List;

public interface NodeHistoryEntryRepository extends JpaRepository<NodeHistoryEntry, Long> {
    
    /**
     * Get history for a specific node
     */
    Page<NodeHistoryEntry> findByZoneAndNetworkAndNodeOrderByChangeDateDesc(
            Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Get history for a specific network
     */
    Page<NodeHistoryEntry> findByZoneAndNetworkOrderByChangeDateDescNodeAsc(
            Integer zone, Integer network, Pageable pageable);

    /**
     * Get history for a specific zone
     */
    Page<NodeHistoryEntry> findByZoneOrderByChangeDateDescNetworkAscNodeAsc(
            Integer zone, Pageable pageable);

    /**
     * Get all history entries
     */
    Page<NodeHistoryEntry> findAllByOrderByChangeDateDescZoneAscNetworkAscNodeAsc(Pageable pageable);

    /**
     * Get changes for a specific date
     */
    List<NodeHistoryEntry> findByChangeDateOrderByZoneAscNetworkAscNodeAsc(LocalDate changeDate);

    /**
     * Get changes between dates
     */
    Page<NodeHistoryEntry> findByChangeDateBetweenOrderByChangeDateDescZoneAscNetworkAscNodeAsc(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Get changes by type
     */
    Page<NodeHistoryEntry> findByChangeTypeOrderByChangeDateDescZoneAscNetworkAscNodeAsc(
            NodeHistoryEntry.ChangeType changeType, Pageable pageable);

    /**
     * Get summary statistics for a date range
     */
    @Query("""
        SELECT new ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto(
            h.changeDate, h.nodelistYear, h.nodelistName,
                (CASE WHEN h.changeType = 'ADDED' THEN 1 ELSE 0 END),
                (CASE WHEN h.changeType = 'REMOVED' THEN 1 ELSE 0 END),
                (CASE WHEN h.changeType = 'MODIFIED' THEN 1 ELSE 0 END),
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
     * Get most active nodes (nodes with most changes)
     */
    @Query("""
        SELECT h.zone, h.network, h.node, COUNT(h) as changeCount
        FROM NodeHistoryEntry h
        WHERE h.changeDate BETWEEN :startDate AND :endDate
        GROUP BY h.zone, h.network, h.node
        ORDER BY changeCount DESC
        """)
    List<Object[]> getMostActiveNodes(LocalDate startDate, LocalDate endDate, Pageable pageable);
}