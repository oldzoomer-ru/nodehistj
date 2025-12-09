package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import org.springframework.data.repository.CrudRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;

/**
 * Repository interface for NodelistEntry entities.
 * Provides methods to find NodelistEntry entities based on various criteria.
 */
public interface NodelistEntryRepository extends CrudRepository<NodelistEntry, Long> {
    /**
     * Find first NodelistEntry entity.
     * @return a NodelistEntry entity matching the criteria
     */
    NodelistEntry findFirstBy();
}