package ru.oldzoomer.nodehistj_history_diff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {
    
    @Query("from NodeEntry where nodelistEntry.nodelistName = :nodelistName " +
            "and nodelistEntry.nodelistYear = :nodelistYear " +
            "order by zone, network, node")
    @EntityGraph(attributePaths = "nodelistEntry")
    List<NodeEntry> findByNodelistYearAndName(Integer nodelistYear, String nodelistName);

    @Query("SELECT DISTINCT ne.nodelistEntry.nodelistYear, ne.nodelistEntry.nodelistName " +
            "FROM NodeEntry ne " +
            "ORDER BY ne.nodelistEntry.nodelistYear DESC, ne.nodelistEntry.nodelistName DESC")
    List<Object[]> findAllNodelistVersions();
}