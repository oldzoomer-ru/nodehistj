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
    @Query("""
            SELECT * FROM nodelist_entry nl
            JOIN node_entry n
            ON nl.id = n.nodelist_entry_id
            ORDER BY nodelist_year DESC, nodelist_name DESC
            """)
    Stream<NodelistEntry> findAllAsStreamWithSort();

    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);
}