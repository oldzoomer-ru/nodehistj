package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;

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
    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone and network = :network and node = :node")
    @EntityGraph("NodeEntry.nodelistEntry")
    NodeEntry find(Integer zone, Integer network, Integer node, String nodelistName, Integer nodelistYear);

    /**
     * Finds a list of NodeEntry entities based on zone, network, nodelist name, and nodelist year.
     *
     * @param zone the zone of the nodes
     * @param network the network of the nodes
     * @param nodelistName the name of the nodelist
     * @param nodelistYear the year of the nodelist
     * @return the list of found NodeEntry entities
     */
    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone and network = :network order by zone, network, node")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> find(Integer zone, Integer network, String nodelistName, Integer nodelistYear);

    /**
     * Finds a list of NodeEntry entities based on zone, nodelist name, and nodelist year.
     *
     * @param zone the zone of the nodes
     * @param nodelistName the name of the nodelist
     * @param nodelistYear the year of the nodelist
     * @return the list of found NodeEntry entities
     */
    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "and zone = :zone order by zone, network, node")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> find(Integer zone, String nodelistName, Integer nodelistYear);

    /**
     * Finds all NodeEntry entities based on nodelist name and nodelist year.
     *
     * @param nodelistName the name of the nodelist
     * @param nodelistYear the year of the nodelist
     * @return the list of all found NodeEntry entities
     */
    @Query("from NodeEntry ne " +
            "where ne.nodelistEntry.nodelistName = :nodelistName " +
            "and ne.nodelistEntry.nodelistYear = :nodelistYear " +
            "order by zone, network, node")
    @EntityGraph("NodeEntry.nodelistEntry")
    List<NodeEntry> findAll(String nodelistName, Integer nodelistYear);
}