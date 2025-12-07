package ru.oldzoomer.nodehistj_history_diff.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.oldzoomer.nodehistj_history_diff.entity.NodelistEntry;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository
        extends PagingAndSortingRepository<NodelistEntry, Long>, CrudRepository<NodelistEntry, Long> {
    /**
     * Checks if a NodelistEntry entity exists based on the nodelist year and name.
     *
     * @param nodelistYear the year of the nodelist
     * @param nodelistName the name of the nodelist
     * @return true if the NodelistEntry entity exists, false otherwise
     */
    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);
}