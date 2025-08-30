package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository extends JpaRepository<NodelistEntry, Long> {
    /**
     * Checks if a NodelistEntry entity exists based on the nodelist year and name.
     *
     * @param nodelistYear the year of the nodelist
     * @param nodelistName the name of the nodelist
     * @return true if the NodelistEntry entity exists, false otherwise
     */
    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);

    /**
     * Finds a list of NodelistEntry entities within a range of years.
     *
     * @param startYear the start year of the range
     * @param endYear the end year of the range
     * @return the list of found NodelistEntry entities
     */
    List<NodelistEntry> findByNodelistYearBetween(Integer startYear, Integer endYear);
}