package ru.oldzoomer.nodehistj_newest_nodelists.service;

import java.util.Set;

import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;

/**
 * Nodelist service layer interface.
 * Provides methods for retrieving nodelist entries based on various criteria.
 */
public interface NodelistService {
    /**
     * Gets all nodelist entries.
     *
     * @return a set of NodeEntryDto objects representing the nodelist entries
     */
    Set<NodeEntryDto> getNodelistEntries();

    /**
     * Gets nodelist entries for a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    Set<NodeEntryDto> getNodelistEntry(Integer zone);

    /**
     * Gets nodelist entries for a specific network within a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    Set<NodeEntryDto> getNodelistEntry(Integer zone, Integer network);

    /**
     * Gets a specific nodelist entry for a node within a specific network and zone.
     *
     * @param zone the zone of the nodelist entry
     * @param network the network of the nodelist entry
     * @param node the node address of the nodelist entry
     * @return a NodeEntryDto object representing the specific nodelist entry
     */
    NodeEntryDto getNodelistEntry(Integer zone, Integer network, Integer node);
}
