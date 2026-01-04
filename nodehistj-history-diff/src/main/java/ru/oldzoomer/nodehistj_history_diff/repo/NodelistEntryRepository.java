package ru.oldzoomer.nodehistj_history_diff.repo;

import java.util.stream.Stream;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import ru.oldzoomer.nodehistj_history_diff.entity.NodelistEntry;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository extends CrudRepository<NodelistEntry, Long> {
    /**
     * Finds all NodelistEntry entities in the database, ordered by nodelist_year and nodelist_name.
     * 
     * @return Stream of NodelistEntry entities
     */
    @Query("""
            SELECT * FROM nodelist_entry
            ORDER BY nodelist_year DESC, day_of_year DESC
            """)
    Stream<NodelistEntry> findAllAsStreamWithSort();


    /**
     * Checks if a NodelistEntry with the given nodelist_year and nodelist_name exists.
     * 
     * @return true if the NodelistEntry exists, false otherwise
     */
    boolean existsByNodelistYearAndDayOfYear(Integer nodelistYear, Integer dayOfYear);
}