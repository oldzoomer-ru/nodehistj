package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {

    @Query("from NodeEntry ne join fetch ne.nodelistEntry nle " +
            "where zone = :zone and network = :network and node = :node")
    NodeEntry getLast(Integer zone, Integer network, Integer node);

    @Query("from NodeEntry ne join fetch ne.nodelistEntry nle " +
            "where zone = :zone and network = :network " +
            "order by zone, network, node")
    List<NodeEntry> getLast(Integer zone, Integer network);

    @Query("from NodeEntry ne join fetch ne.nodelistEntry nle " +
            "where zone = :zone order by zone, network, node")
    List<NodeEntry> getLast(Integer zone);

    @Query("from NodeEntry ne join fetch ne.nodelistEntry nle " +
            "order by zone, network, node")
    List<NodeEntry> getAll();
}