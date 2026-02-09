package ru.oldzoomer.nodehistj_history_diff.repo;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

/**
 * Repository interface for NodeHistoryEntry entities.
 * Provides methods to find NodeHistoryEntry entities based on various criteria.
 */
public interface NodeHistoryEntryRepository
        extends PagingAndSortingRepository<@NonNull NodeHistoryEntry, @NonNull Long>,
        CrudRepository<@NonNull NodeHistoryEntry, @NonNull Long> {

    /**
     * Gets history for a specific node.
     *
     * @param zone the zone of the node
     * @param network the network of the node
     * @param node the node number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities matching the criteria
     */
    Page<@NonNull NodeHistoryEntry> findByZoneAndNetworkAndNode(
            Integer zone, Integer network, Integer node, Pageable pageable);

    /**
     * Gets history for a specific network.
     *
     * @param zone the zone of the network
     * @param network the network number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities matching the criteria
     */
    Page<@NonNull NodeHistoryEntry> findByZoneAndNetwork(Integer zone, Integer network, Pageable pageable);

    /**
     * Gets history for a specific zone.
     *
     * @param zone the zone number
     * @param pageable the pagination information
     * @return a page of NodeHistoryEntry entities matching the criteria
     */
    Page<@NonNull NodeHistoryEntry> findByZone(Integer zone, Pageable pageable);
}