package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {
    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone and network = :network and node = :node")
    @EntityGraph("NodeEntry.nodelistEntry")
    NodeEntry find(Integer zone, Integer network, Integer node, String nodelistName, Integer nodelistYear);

    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone and network = :network order by zone, network, node")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> find(Integer zone, Integer network, String nodelistName, Integer nodelistYear);

    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone order by zone, network, node")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> find(Integer zone, String nodelistName, Integer nodelistYear);

    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "order by zone, network, node")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findAll(String nodelistName, Integer nodelistYear);
}