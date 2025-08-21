package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;

@Repository
public interface NodeEntryRepository extends CassandraRepository<NodeEntry, NodeEntry.NodeEntryKey> {

    @Cacheable("historicNodeEntriesByZone")
    @Query("SELECT * FROM node_entry WHERE zone = ?0 ALLOW FILTERING")
    List<NodeEntry> findByZone(Integer zone);

    @AllowFiltering
    @Cacheable("historicNodeEntries")
    @Query("SELECT * FROM node_entry WHERE zone = ?0 AND network = ?1 ALLOW FILTERING")
    List<NodeEntry> findByZoneAndNetwork(Integer zone, Integer network);

    @AllowFiltering
    @Cacheable("historicNodeEntry")
    @Query("""
            SELECT * FROM node_entry WHERE zone = ?0 AND network = ?1
            AND node = ?2ALLOW FILTERING
            """)
    NodeEntry findByZoneAndNetworkAndNode(Integer zone, Integer network, Integer node);
}