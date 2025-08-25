package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntryKey;

public interface NodeEntryRepository extends CassandraRepository<NodeEntry, NodeEntryKey> {

    @Query("SELECT * FROM node_entry WHERE nodelist_year = :nodelistYear " +
            "AND nodelist_name = :nodelistName")
    @Cacheable(value = "historicNodelistRequests", unless = "#result == null || #result.isEmpty()")
    List<NodeEntry> findByNodelistYearAndName(@Param("nodelistName") String nodelistName,
            @Param("nodelistYear") Integer nodelistYear);

    @Query("SELECT * FROM node_entry " +
            "WHERE zone = :zone AND nodelist_year = :nodelistYear " +
            "AND nodelist_name = :nodelistName")
    @Cacheable(value = "historicNodelistRequests", unless = "#result == null || #result.isEmpty()")
    List<NodeEntry> findByNodelistYearAndNameAndZone(@Param("nodelistName") String nodelistName,
            @Param("nodelistYear") Integer nodelistYear,
            @Param("zone") Integer zone);

    @Query("SELECT * FROM node_entry " +
            "WHERE zone = :zone AND network = :network " +
            "AND nodelist_year = :nodelistYear AND nodelist_name = :nodelistName")
    @Cacheable(value = "historicNodelistRequests", unless = "#result == null || #result.isEmpty()")
    List<NodeEntry> findByNodelistYearAndNameAndZoneAndNetwork(@Param("nodelistName") String nodelistName,
            @Param("nodelistYear") Integer nodelistYear,
            @Param("zone") Integer zone,
            @Param("network") Integer network);

    @Query("SELECT * FROM node_entry " +
            "WHERE zone = :zone AND network = :network AND node = :node " +
            "AND nodelist_year = :nodelistYear AND nodelist_name = :nodelistName")
    @Cacheable(value = "historicNodelistRequests", unless = "#result == null")
    NodeEntry findByNodelistYearAndNameAndZoneAndNetworkAndNode(@Param("nodelistName") String nodelistName,
            @Param("nodelistYear") Integer nodelistYear,
            @Param("zone") Integer zone,
            @Param("network") Integer network,
            @Param("node") Integer node);
}