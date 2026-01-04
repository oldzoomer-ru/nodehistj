package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.time.Year;

import org.springframework.data.repository.CrudRepository;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository extends CrudRepository<NodelistEntry, Long> {
    /**
     * Find NodelistEntry entity for a given nodelist year and name.
     * @param nodelistYear the year of the nodelist
     * @param dayOfYear the name of the nodelist
     * @return a NodelistEntry entity matching the criteria
     */
    NodelistEntry findFirstByNodelistYearAndDayOfYear(Year nodelistYear, Integer dayOfYear);

    boolean existsByNodelistYearAndDayOfYear(Year nodelistYear, Integer dayOfYear);
}