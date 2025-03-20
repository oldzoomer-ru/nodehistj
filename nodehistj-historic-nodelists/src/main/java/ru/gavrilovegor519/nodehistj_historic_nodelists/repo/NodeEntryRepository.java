package ru.gavrilovegor519.nodehistj_historic_nodelists.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodeEntry;

import java.util.List;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {
    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistName = :nodelistName and n.nodelistYear = :nodelistYear " +
            " and e.zone = :zone and e.network = :network and e.node = :node")
    NodeEntry get(Integer zone, Integer network, Integer node, String nodelistName, Integer nodelistYear);

    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistName = :nodelistName and n.nodelistYear = :nodelistYear " +
            " and e.zone = :zone and e.network = :network order by e.zone asc, e.network asc, e.node asc")
    List<NodeEntry> get(Integer zone, Integer network, String nodelistName, Integer nodelistYear);

    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistName = :nodelistName and n.nodelistYear = :nodelistYear " +
            "and e.zone = :zone order by e.zone asc, e.network asc, e.node asc")
    List<NodeEntry> get(Integer zone, String nodelistName, Integer nodelistYear);

    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistName = :nodelistName and n.nodelistYear = :nodelistYear " +
            "order by e.zone asc, e.network asc, e.node asc")
    List<NodeEntry> getAll(String nodelistName, Integer nodelistYear);

    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistYear = (select max(n1.nodelistYear) from NodelistEntry n1) " +
            "and n.nodelistName = (select max(n1.nodelistName) from NodelistEntry n1) " +
            "and e.zone = :zone and e.network = :network and e.node = :node")
    NodeEntry getLast(Integer zone, Integer network, Integer node);

    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistYear = (select max(n1.nodelistYear) from NodelistEntry n1) " +
            "and n.nodelistName = (select max(n1.nodelistName) from NodelistEntry n1) " +
            "and e.zone = :zone and e.network = :network order by e.zone asc, e.network asc, e.node asc")
    List<NodeEntry> getLast(Integer zone, Integer network);

    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistYear = (select max(n1.nodelistYear) from NodelistEntry n1) " +
            "and n.nodelistName = (select max(n1.nodelistName) from NodelistEntry n1) " +
            "and e.zone = :zone order by e.zone asc, e.network asc, e.node asc")
    List<NodeEntry> getLast(Integer zone);

    @Query("select e, n from NodeEntry e join fetch e.nodelistEntry n " +
            "where n.nodelistYear = (select max(n1.nodelistYear) from NodelistEntry n1) " +
            "and n.nodelistName = (select max(n1.nodelistName) from NodelistEntry n1) " +
            "order by e.zone asc, e.network asc, e.node asc")
    List<NodeEntry> getAll();
}