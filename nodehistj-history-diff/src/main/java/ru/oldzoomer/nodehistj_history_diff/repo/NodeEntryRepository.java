package ru.oldzoomer.nodehistj_history_diff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;

/**
 * Repository interface for NodeEntry entities.
 * Provides methods to find NodeEntry entities based on various criteria.
 */
public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {

    /**
     * Finds NodeEntry entities based on nodelist year and name.
     *
     * @param nodelistYear the year of the nodelist
     * @param nodelistName the name of the nodelist
     * @return a list of NodeEntry entities matching the criteria
     */
    @EntityGraph("NodeEntry.nodelistEntry")
    @Query("SELECT ne FROM NodeEntry ne " +
           "WHERE ne.nodelistEntry.nodelistYear = ?1 " +
           "AND ne.nodelistEntry.nodelistName = ?2")
    List<NodeEntry> findByNodelist(Integer nodelistYear, String nodelistName);

    /**
     * Finds all nodelist versions.
     *
     * @return a list of Object arrays containing nodelist year and name
     */
    @Query("SELECT DISTINCT ne.nodelistEntry.nodelistYear, ne.nodelistEntry.nodelistName " +
            "FROM NodeEntry ne " +
            "ORDER BY ne.nodelistEntry.nodelistYear DESC, ne.nodelistEntry.nodelistName DESC")
    List<Object[]> findAllNodelistVersions();
}