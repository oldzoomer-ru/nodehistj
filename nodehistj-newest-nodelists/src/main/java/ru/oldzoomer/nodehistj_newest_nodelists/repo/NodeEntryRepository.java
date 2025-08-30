package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;

/**
 * Repository interface for NodeEntry entities.
 * Provides methods to find NodeEntry entities based on various criteria.
 */
public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {

    /**
     * Finds the most recent NodeEntry entity based on zone, network, and node.
     *
     * @param zone the zone of the node
     * @param network the network of the node
     * @param node the node number
     * @return the most recent NodeEntry entity matching the criteria
     */
    @EntityGraph("NodeEntry.nodelistEntry")
    NodeEntry findFirstByZoneAndNetworkAndNodeOrderByIdDesc(Integer zone, Integer network, Integer node);

    /**
     * Finds all NodeEntry entities based on zone and network.
     *
     * @param zone the zone of the nodes
     * @param network the network of the nodes
     * @return a list of NodeEntry entities matching the criteria
     */
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findByZoneAndNetworkOrderByIdDesc(Integer zone, Integer network);

    /**
     * Finds all NodeEntry entities based on zone.
     *
     * @param zone the zone of the nodes
     * @return a list of NodeEntry entities matching the criteria
     */
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findByZoneOrderByIdDesc(Integer zone);

    /**
     * Finds all NodeEntry entities.
     *
     * @return a list of all NodeEntry entities
     */
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findAll();
}