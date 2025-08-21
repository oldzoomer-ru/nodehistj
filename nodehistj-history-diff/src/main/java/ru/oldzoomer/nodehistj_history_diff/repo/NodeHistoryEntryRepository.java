package ru.oldzoomer.nodehistj_history_diff.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

public interface NodeHistoryEntryRepository 
        extends CassandraRepository<NodeHistoryEntry, NodeHistoryEntry.NodeHistoryEntryKey> {

    @Query("""
            SELECT * FROM node_history_entry
            WHERE zone = :zone AND network = :network AND node = :node
            ORDER BY nodelist_year DESC, nodelist_name DESC, id DESC
            """)
    @Cacheable(value = "diffNodeHistoryEntries", unless = "#result == null || #result.isEmpty()")
    Slice<NodeHistoryEntry> findByZoneAndNetworkAndNode(
            @Param("zone") Integer zone,
            @Param("network") Integer network,
            @Param("node") Integer node,
            Pageable pageable);

    @Query("""
            SELECT * FROM node_history_entry_by_zone_network
            WHERE zone = :zone AND network = :network
            ORDER BY nodelist_year DESC, nodelist_name DESC, node DESC, id DESC
            """)
    Slice<NodeHistoryEntry> findByZoneAndNetwork(
            @Param("zone") Integer zone,
            @Param("network") Integer network,
            Pageable pageable);

    @Query("""
            SELECT * FROM node_history_entry_by_zone
            WHERE zone = :zone
            ORDER BY nodelist_year DESC, nodelist_name DESC,
            network DESC, node DESC, id DESC
            """)
    Slice<NodeHistoryEntry> findByZone(
            @Param("zone") Integer zone,
            Pageable pageable);

    @Query("""
            SELECT * FROM node_history_entry
            WHERE zone = :zone AND network = :network AND node = :node
            AND nodelist_year = :year AND nodelist_name = :name
            LIMIT 1
            """)
    boolean existsByZoneAndNetworkAndNode(
            @Param("zone") Integer zone,
            @Param("network") Integer network,
            @Param("node") Integer node,
            @Param("year") Integer year,
            @Param("name") String name);

    @Query("""
            SELECT * FROM node_history_entry_by_zone_network
            WHERE zone = :zone AND network = :network AND node = :node
            AND nodelist_year = :year AND nodelist_name = :name
            LIMIT 1
            """)
    boolean existsByZoneAndNetworkAndNodeOptimized(
            @Param("zone") Integer zone,
            @Param("network") Integer network,
            @Param("node") Integer node,
            @Param("year") Integer year,
            @Param("name") String name);
}