package ru.oldzoomer.nodehistj_history_diff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;

public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {

    @Query("select ne from NodeEntry ne join fetch ne.nodelistEntry nle " +
            "where nle.nodelistName = :nodelistName " +
            "and nle.nodelistYear = :nodelistYear " +
            "order by zone, network, node")
    List<NodeEntry> findByNodelistYearAndName(Integer nodelistYear, String nodelistName);

    @Query("SELECT DISTINCT ne.nodelistEntry.nodelistYear, ne.nodelistEntry.nodelistName " +
            "FROM NodeEntry ne " +
            "ORDER BY ne.nodelistEntry.nodelistYear DESC, ne.nodelistEntry.nodelistName DESC")
    List<Object[]> findAllNodelistVersions();
}