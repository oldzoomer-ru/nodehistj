package ru.oldzoomer.nodehistj_history_diff.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;

public interface NodeEntryRepository extends CassandraRepository<NodeEntry, UUID> {

    @Query("SELECT * FROM node_entry WHERE nodelist_year = :nodelistYear AND nodelist_name = :nodelistName " +
           "ORDER BY zone, network, node")
    List<NodeEntry> findByNodelistYearAndName(@Param("nodelistYear") Integer nodelistYear,
                                              @Param("nodelistName") String nodelistName);

    @Query("SELECT DISTINCT nodelist_year, nodelist_name FROM node_entry " +
           "ORDER BY nodelist_year DESC, nodelist_name DESC")
    List<Object[]> findAllNodelistVersions();

    List<NodeEntry> findByZoneAndNetworkAndNode(Integer zone, Integer network, Integer node);
    
    List<NodeEntry> findByNodelistYear(Integer nodelistYear);
}