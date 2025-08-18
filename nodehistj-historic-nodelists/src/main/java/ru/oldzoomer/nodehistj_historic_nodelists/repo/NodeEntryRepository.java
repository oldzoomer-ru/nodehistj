package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;

public interface NodeEntryRepository extends CassandraRepository<NodeEntry, UUID> {

    @Query("SELECT * FROM node_entry " +
           "WHERE nodelist_name = ?0 " +
           "AND nodelist_year = ?1 " +
           "AND zone = ?2 " +
           "AND network = ?3 " +
           "AND node = ?4")
    NodeEntry get(String nodelistName, Integer nodelistYear, Integer zone, Integer network, Integer node);

    @Query("SELECT * FROM node_entry " +
           "WHERE nodelist_name = ?0 " +
           "AND nodelist_year = ?1 " +
           "AND zone = ?2 " +
           "AND network = ?3 " +
           "ORDER BY zone, network, node")
    List<NodeEntry> get(String nodelistName, Integer nodelistYear, Integer zone, Integer network);

    @Query("SELECT * FROM node_entry " +
           "WHERE nodelist_name = ?0 " +
           "AND nodelist_year = ?1 " +
           "AND zone = ?2 " +
           "ORDER BY zone, network, node")
    List<NodeEntry> get(String nodelistName, Integer nodelistYear, Integer zone);

    @Query("SELECT * FROM node_entry WHERE nodelist_name = ?0 AND nodelist_year = ?1 ORDER BY zone, network, node")
    List<NodeEntry> getAll(String nodelistName, Integer nodelistYear);

    @Query("SELECT * FROM node_entry WHERE nodelist_name = ?0 AND nodelist_year = ?1")
    List<NodeEntry> findByNodelistYearAndName(String nodelistName, Integer nodelistYear);
}