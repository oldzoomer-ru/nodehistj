package ru.gavrilovegor519.nodehistj_historic_nodelists.service;

import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodelistDto;

import java.util.List;

/**
 * Nodelist service layer interface
 */
public interface NodelistService {
    /**
     * Get all nodelist entries
     *
     * @return List of nodelist entries
     */
    List<NodelistDto> getNodelistEntries();

    /**
     * Get zone nodelist entry
     *
     * @param zone zone
     * @return Zone nodelist entry
     */
    List<NodelistDto> getNodelistEntry(int zone);

    /**
     * Get network nodelist entry
     *
     * @param zone    zone
     * @param network network
     * @return Network nodelist entry
     */
    List<NodelistDto> getNodelistEntry(int zone, int network);

    /**
     * Get node nodelist entry
     *
     * @param zone    zone
     * @param network network
     * @param node    node address
     * @return Node nodelist entry
     */
    NodelistDto getNodelistEntry(int zone, int network, int node);
}
