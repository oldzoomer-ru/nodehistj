package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntryKey;

public interface NodeEntryRepository extends CassandraRepository<NodeEntry, NodeEntryKey> {

    @Cacheable(value = "findByZone", unless = "#result == null || #result.isEmpty()")
    @Query("SELECT * FROM node_entry WHERE zone = :zone")
    List<NodeEntry> findByZone(
        @Param("zone") Integer zone);

    @Cacheable(value = "findByZoneAndNetwork", unless = "#result == null || #result.isEmpty()")
    @Query("SELECT * FROM node_entry WHERE zone = :zone AND network = :network")
    List<NodeEntry> findByZoneAndNetwork(
        @Param("zone") Integer zone, 
        @Param("network") Integer network);

    @Cacheable(value = "findByZoneAndNetworkAndNode", unless = "#result == null")
    @Query("SELECT * FROM node_entry " +
            "WHERE zone = :zone AND network = :network AND node = :node LIMIT 1")
    NodeEntry findByZoneAndNetworkAndNode(
        @Param("zone") Integer zone, 
        @Param("network") Integer network, 
        @Param("node") Integer node);
}