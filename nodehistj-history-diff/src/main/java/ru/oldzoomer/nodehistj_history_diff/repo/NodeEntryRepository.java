package ru.oldzoomer.nodehistj_history_diff.repo;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;

@Repository
public interface NodeEntryRepository extends CassandraRepository<NodeEntry, NodeEntry.NodeEntryKey> {

    @Query("SELECT * FROM node_entry WHERE nodelist_year = :nodelistYear AND nodelist_name = :nodelistName " +
           "ORDER BY zone, network, node")
    @Cacheable(value = "diffNodeEntriesByVersion", unless = "#result == null || #result.isEmpty()")
    List<NodeEntry> findByNodelistYearAndName(@Param("nodelistYear") Integer nodelistYear,
                                              @Param("nodelistName") String nodelistName);

    @Query("SELECT DISTINCT nodelist_year, nodelist_name FROM node_entry " +
           "ORDER BY nodelist_year DESC, nodelist_name DESC")
    @Cacheable(value = "diffNodelistVersions", unless = "#result == null || #result.isEmpty()")
    List<Object[]> findAllNodelistVersions();

}
