package ru.gavrilovegor519.nodehistj_historic_nodelists.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodeEntry;

import java.util.List;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {
    @Query("from NodeEntry where nodelistEntry.nodelistName = :nodelistName " +
            "and nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone and network = :network and node = :node")
    @EntityGraph(attributePaths = "nodelistEntry")
    NodeEntry get(Integer zone, Integer network, Integer node, String nodelistName, Integer nodelistYear);

    @Query("from NodeEntry where nodelistEntry.nodelistName = :nodelistName " +
            "and nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone and network = :network order by zone, network, node")
    @EntityGraph(attributePaths = "nodelistEntry")
    List<NodeEntry> get(Integer zone, Integer network, String nodelistName, Integer nodelistYear);

    @Query("from NodeEntry where nodelistEntry.nodelistName = :nodelistName " +
            "and nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone order by zone, network, node")
    @EntityGraph(attributePaths = "nodelistEntry")
    List<NodeEntry> get(Integer zone, String nodelistName, Integer nodelistYear);

    @Query("from NodeEntry where nodelistEntry.nodelistName = :nodelistName " +
            "and nodelistEntry.nodelistYear = :nodelistYear " +
            "order by zone, network, node")
    @EntityGraph(attributePaths = "nodelistEntry")
    List<NodeEntry> getAll(String nodelistName, Integer nodelistYear);

    @Query("from NodeEntry where nodelistEntry.nodelistName = (select max(nodelistName) from NodelistEntry) " +
            "and nodelistEntry.nodelistYear = (select max(nodelistYear) from NodelistEntry) " +
            "and zone = :zone and network = :network and node = :node")
    @EntityGraph(attributePaths = "nodelistEntry")
    NodeEntry getLast(Integer zone, Integer network, Integer node);

    @Query("from NodeEntry where nodelistEntry.nodelistName = (select max(nodelistName) from NodelistEntry) " +
            "and nodelistEntry.nodelistYear = (select max(nodelistYear) from NodelistEntry) " +
            "and zone = :zone and network = :network order by zone, network, node")
    @EntityGraph(attributePaths = "nodelistEntry")
    List<NodeEntry> getLast(Integer zone, Integer network);

    @Query("from NodeEntry where nodelistEntry.nodelistName = (select max(nodelistName) from NodelistEntry) " +
            "and nodelistEntry.nodelistYear = (select max(nodelistYear) from NodelistEntry) " +
            "and zone = :zone order by zone, network, node")
    @EntityGraph(attributePaths = "nodelistEntry")
    List<NodeEntry> getLast(Integer zone);

    @Query("from NodeEntry where nodelistEntry.nodelistName = (select max(nodelistName) from NodelistEntry) " +
            "and nodelistEntry.nodelistYear = (select max(nodelistYear) from NodelistEntry) " +
            "order by zone, network, node")
    @EntityGraph(attributePaths = "nodelistEntry")
    List<NodeEntry> getAll();
}