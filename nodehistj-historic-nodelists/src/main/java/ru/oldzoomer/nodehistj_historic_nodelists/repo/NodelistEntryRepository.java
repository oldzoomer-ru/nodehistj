package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import org.springframework.data.repository.CrudRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;

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
    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);

    /**
     * Find NodelistEntry entity for a given nodelist year and name.
     * @param nodelistYear the year of the nodelist
     * @param nodelistName the name of the nodelist
     * @return a NodelistEntry entity matching the criteria
     */
    NodelistEntry findFirstByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);
}