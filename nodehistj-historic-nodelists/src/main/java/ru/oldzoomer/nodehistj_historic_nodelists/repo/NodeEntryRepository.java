package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;

import java.util.List;

/**
 * Repository interface for NodeEntry entities.
 * Provides methods to find NodeEntry entities based on various criteria.
 */
public interface NodeEntryRepository extends JpaRepository<NodeEntry, Long> {
    /**
     * Finds a NodeEntry entity based on zone, network, node, nodelist name, and nodelist year.
     *
     * @param zone the zone of the node
     * @param network the network of the node
     * @param node the node number
     * @param nodelistName the name of the nodelist
     * @param nodelistYear the year of the nodelist
     * @return the found NodeEntry entity
     */
    @SuppressWarnings("checkstyle:MethodName")
    @EntityGraph("NodeEntry.nodelistEntry")
    NodeEntry findByZoneAndNetworkAndNodeAndNodelistEntry_NodelistNameAndNodelistEntry_NodelistYear(
            Integer zone, Integer network, Integer node, String nodelistName, Integer nodelistYear);

    /**
     * Finds a list of NodeEntry entities based on zone, network, nodelist name, and nodelist year.
     *
     * @param zone the zone of the nodes
     * @param network the network of the nodes
     * @param nodelistName the name of the nodelist
     * @param nodelistYear the year of the nodelist
     * @return the list of found NodeEntry entities
     */
    @SuppressWarnings("checkstyle:MethodName")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findAllByZoneAndNetworkAndNodelistEntry_NodelistNameAndNodelistEntry_NodelistYear(
            Integer zone, Integer network, String nodelistName, Integer nodelistYear);

    /**
     * Finds a list of NodeEntry entities based on zone, nodelist name, and nodelist year.
     *
     * @param zone the zone of the nodes
     * @param nodelistName the name of the nodelist
     * @param nodelistYear the year of the nodelist
     * @return the list of found NodeEntry entities
     */
    @SuppressWarnings("checkstyle:MethodName")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findAllByZoneAndNodelistEntry_NodelistNameAndNodelistEntry_NodelistYear(
            Integer zone, String nodelistName, Integer nodelistYear);

    /**
     * Finds all NodeEntry entities based on nodelist name and nodelist year.
     *
     * @param nodelistName the name of the nodelist
     * @param nodelistYear the year of the nodelist
     * @return the list of all found NodeEntry entities
     */
    @SuppressWarnings("checkstyle:MethodName")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findAllByNodelistEntry_NodelistNameAndNodelistEntry_NodelistYear(
            String nodelistName, Integer nodelistYear);
}