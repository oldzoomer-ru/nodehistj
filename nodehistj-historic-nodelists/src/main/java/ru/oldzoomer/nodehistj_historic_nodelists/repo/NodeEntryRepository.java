package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {

    @EntityGraph(attributePaths = {"nodelistEntry"})
    List<NodeEntry> findByYearAndDayOfYear(int year, int dayOfYear);
    
    @EntityGraph(attributePaths = {"nodelistEntry"})
    List<NodeEntry> findByYearAndDayOfYearAndZone(int year, int dayOfYear, int zone);
    
    @EntityGraph(attributePaths = {"nodelistEntry"})
    List<NodeEntry> findByYearAndDayOfYearAndZoneAndNetwork(int year, int dayOfYear, int zone, int network);
    
    @EntityGraph(attributePaths = {"nodelistEntry"})
    NodeEntry findByYearAndDayOfYearAndZoneAndNetworkAndNode(int year, int dayOfYear, int zone, int network, int node);
}