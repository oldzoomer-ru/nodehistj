package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;

import java.util.Set;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository extends CrudRepository<NodelistEntry, Long> {
    /**
     * Checks if a NodelistEntry entity exists based on the nodelist year and name.
     *
     * @param nodelistYear the year of the nodelist
     * @param nodelistName the name of the nodelist
     * @return true if the NodelistEntry entity exists, false otherwise
     */
    @Query("""
            SELECT 1 FROM nodelist_entry nl
            WHERE nl.nodelist_year = :nodelistYear
            AND nl.nodelist_name = :nodelistName
            """)
    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);

    /**
     * Find all NodelistEntry entities for a given nodelist year and name.
     * @return a set of NodelistEntry entities matching the criteria
     */
    @Query("""
            SELECT * FROM nodelist_entry nl
            JOIN node_entry n
            ON nl.id = n.nodelist_entry_id
            ORDER BY nl.nodelist_year DESC, nl.nodelist_name DESC
            LIMIT 1
            """)
    Set<NodelistEntry> findAll();
}