package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {

    @EntityGraph("NodeEntry.nodelistEntry")
    NodeEntry findFirstByZoneAndNetworkAndNodeOrderByIdDesc(Integer zone, Integer network, Integer node);

    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findByZoneAndNetworkOrderByIdDesc(Integer zone, Integer network);

    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findByZoneOrderByIdDesc(Integer zone);

    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findAll();
}