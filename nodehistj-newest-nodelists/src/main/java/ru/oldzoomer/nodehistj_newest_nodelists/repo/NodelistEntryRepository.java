package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;

import java.util.List;

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
    @Query("""
            SELECT 1 FROM nodelist_entry nl
            WHERE nl.nodelist_year = :nodelistYear
            AND nl.nodelist_name = :nodelistName
            """)
    boolean existsByYearAndName(Integer nodelistYear, String nodelistName);

    /**
     * Finds a NodeEntry entity based on zone, network, node, nodelist name, and nodelist year.
     *
     * @param zone    the zone of the node
     * @param network the network of the node
     * @param node    the node number
     * @return the found NodeEntry entity
     */
    @SuppressWarnings("checkstyle:MethodName")
    @Query("""
            SELECT * FROM node_entry n
            JOIN nodelist_entry nl
            ON n.nodelist_entry_id = nl.id
            WHERE n.zone = :zone
            AND n.network = :network
            AND n.node = :node
            ORDER BY nl.nodelist_year DESC, nl.nodelist_name DESC
            LIMIT 1
            """)
    NodeEntry findByNode(Integer zone, Integer network, Integer node);

    /**
     * Finds a list of NodeEntry entities based on zone, network, nodelist name, and nodelist year.
     *
     * @param zone    the zone of the nodes
     * @param network the network of the nodes
     * @return the list of found NodeEntry entities
     */
    @SuppressWarnings("checkstyle:MethodName")
    @Query("""
            SELECT * FROM node_entry n
            JOIN nodelist_entry nl
            ON n.nodelist_entry_id = nl.id
            WHERE n.zone = :zone
            AND n.network = :network
            ORDER BY nl.nodelist_year DESC, nl.nodelist_name DESC
            LIMIT 1
            """)
    List<NodeEntry> findByNetwork(Integer zone, Integer network);

    /**
     * Finds a list of NodeEntry entities based on zone, nodelist name, and nodelist year.
     *
     * @param zone the zone of the nodes
     * @return the list of found NodeEntry entities
     */
    @SuppressWarnings("checkstyle:MethodName")
    @Query("""
            SELECT * FROM node_entry n
            JOIN nodelist_entry nl
            ON n.nodelist_entry_id = nl.id
            WHERE n.zone = :zone
            ORDER BY nl.nodelist_year DESC, nl.nodelist_name DESC
            LIMIT 1
            """)
    List<NodeEntry> findByZone(Integer zone);

    /**
     * Finds all NodeEntry entities based on nodelist name and nodelist year.
     *
     * @return the list of all found NodeEntry entities
     */
    @SuppressWarnings("checkstyle:MethodName")
    @Query("""
            SELECT * FROM node_entry n
            JOIN nodelist_entry nl
            ON n.nodelist_entry_id = nl.id
            ORDER BY nl.nodelist_year DESC, nl.nodelist_name DESC
            LIMIT 1
            """)
    List<NodeEntry> findAllNodeEntries();
}