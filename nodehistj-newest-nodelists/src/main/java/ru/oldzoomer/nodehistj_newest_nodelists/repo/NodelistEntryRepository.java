package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.CrudRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository extends CrudRepository<@NonNull NodelistEntry, @NonNull Long> {
    /**
     * Find first NodelistEntry entity.
     * 
     * @return a NodelistEntry entity matching the criteria
     */
    NodelistEntry findFirstBy();

    /**
     * Checks if a NodelistEntry with the given nodelist_year and nodelist_name exists.
     * 
     * @param nodelistYear   the year of the nodelist to check for existence
     * @param dayOfYear      the day of the year in the nodelist to check for existence
     * @return true if the NodelistEntry exists, false otherwise
     */
    boolean existsByNodelistYearAndDayOfYear(Integer nodelistYear, Integer dayOfYear);
}