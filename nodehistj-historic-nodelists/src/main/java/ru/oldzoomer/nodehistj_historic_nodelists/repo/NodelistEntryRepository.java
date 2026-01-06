package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import org.springframework.data.repository.CrudRepository;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository extends CrudRepository<NodelistEntry, Long> {
    /**
     * Find NodelistEntry entity for a given nodelist year and day of year.
     * @param nodelistYear the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @return a NodelistEntry entity matching the criteria
     */
    NodelistEntry findFirstByNodelistYearAndDayOfYear(Integer nodelistYear, Integer dayOfYear);
    
    /**
     * Check if a nodelist entry exists for a given year and day of year.
     * @param nodelistYear the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @return true if a nodelist entry exists, false otherwise
     */
    boolean existsByNodelistYearAndDayOfYear(Integer nodelistYear, Integer dayOfYear);
}