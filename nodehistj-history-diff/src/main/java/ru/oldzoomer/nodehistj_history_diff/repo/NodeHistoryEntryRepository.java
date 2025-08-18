package ru.oldzoomer.nodehistj_history_diff.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry.ChangeType;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto;

public interface NodeHistoryEntryRepository extends CassandraRepository<NodeHistoryEntry, UUID> {

    Slice<NodeHistoryEntry> findByZoneAndNetworkAndNodeOrderByChangeDateDesc(
            Integer zone, Integer network, Integer node, Pageable pageable);

    Slice<NodeHistoryEntry> findByNodelistYearAndNodelistNameOrderByChangeDateDesc(
            @Param("nodelistYear") Integer nodelistYear,
            @Param("nodelistName") String nodelistName,
            Pageable pageable);

    Slice<NodeHistoryEntry> findByZoneAndNetworkOrderByChangeDateDescNodeAsc(
            Integer zone, Integer network, Pageable pageable);

    Slice<NodeHistoryEntry> findByZoneOrderByChangeDateDescNetworkAscNodeAsc(
            Integer zone, Pageable pageable);

    Slice<NodeHistoryEntry> findAllByOrderByChangeDateDescZoneAscNetworkAscNodeAsc(Pageable pageable);

    List<NodeHistoryEntry> findByChangeDateOrderByZoneAscNetworkAscNodeAsc(LocalDate changeDate);

    Slice<NodeHistoryEntry> findByChangeDateBetweenOrderByChangeDateDescZoneAscNetworkAscNodeAsc(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    Slice<NodeHistoryEntry> findByChangeTypeOrderByChangeDateDescZoneAscNetworkAscNodeAsc(
            ChangeType changeType, Pageable pageable);

    @Query("""
        SELECT change_date, nodelist_year, nodelist_name,
            SUM(CASE WHEN change_type = 'ADDED' THEN 1 ELSE 0 END) as addedCount,
            SUM(CASE WHEN change_type = 'REMOVED' THEN 1 ELSE 0 END) as removedCount,
            SUM(CASE WHEN change_type = 'MODIFIED' THEN 1 ELSE 0 END) as modifiedCount,
            COUNT(*) as totalCount
        FROM node_history_entry
        WHERE nodelist_year = :nodelistYear AND nodelist_name = :nodelistName
        GROUP BY change_date, nodelist_year, nodelist_name
        """)
    List<NodeChangeSummaryDto> getChangeSummaryByNodelist(
            @Param("nodelistYear") Integer nodelistYear,
            @Param("nodelistName") String nodelistName);

    @Query("""
        SELECT zone, network, node, COUNT(*) as changeCount
        FROM node_history_entry
        WHERE nodelist_year = :nodelistYear AND nodelist_name = :nodelistName
        GROUP BY zone, network, node
        LIMIT :limit
        """)
    List<Object[]> getMostActiveNodesInNodelist(
            @Param("nodelistYear") Integer nodelistYear,
            @Param("nodelistName") String nodelistName,
            @Param("limit") int limit);

    boolean existsByZoneAndNetworkAndNodeAndNodelistYearAndNodelistName(
            Integer zone, Integer network, Integer node, Integer nodelistYear, String nodelistName);
}