package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;

@Repository
public interface NodeEntryRepository extends CassandraRepository<NodeEntry, UUID> {

    @AllowFiltering
    @Query("SELECT * FROM node_entry WHERE zone = :zone AND network = :network AND node = :node LIMIT 1")
    NodeEntry getLast(
        @Param("zone") Integer zone,
        @Param("network") Integer network,
        @Param("node") Integer node);

    @AllowFiltering
    @Query("SELECT * FROM node_entry WHERE zone = :zone AND network = :network")
    List<NodeEntry> getLast(
        @Param("zone") Integer zone,
        @Param("network") Integer network);

    @AllowFiltering
    @Query("SELECT * FROM node_entry WHERE zone = :zone")
    List<NodeEntry> getLast(@Param("zone") Integer zone);

    @Query("SELECT * FROM node_entry")
    List<NodeEntry> getAll();
}