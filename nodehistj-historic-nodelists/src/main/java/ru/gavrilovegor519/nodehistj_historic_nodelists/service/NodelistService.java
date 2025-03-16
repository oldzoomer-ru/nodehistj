package ru.gavrilovegor519.nodehistj_historic_nodelists.service;

import ru.gavrilovegor519.dto.NodelistEntryDto;

import java.util.Map;

/**
 * Nodelist service layer interface
 */
public interface NodelistService {
    /**
     * Get all nodelist entries
     *
     * @return Map of nodelist entries
     */
    Map<Integer, NodelistEntryDto> getNodelistEntries();

    /**
     * Get zone nodelist entry
     *
     * @param zone zone
     * @return Zone nodelist entry
     */
    NodelistEntryDto getNodelistEntry(int zone);

    /**
     * Get network nodelist entry
     *
     * @param zone zone
     * @param network network
     * @return Network nodelist entry
     */
    NodelistEntryDto getNodelistEntry(int zone, int network);

    /**
     * Get node nodelist entry
     *
     * @param zone zone
     * @param network network
     * @param node node address
     * @return Node nodelist entry
     */
    NodelistEntryDto getNodelistEntry(int zone, int network, int node);
}
