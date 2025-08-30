package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;

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
}