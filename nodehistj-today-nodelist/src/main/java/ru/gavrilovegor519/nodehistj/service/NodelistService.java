package ru.gavrilovegor519.nodehistj.service;

import ru.gavrilovegor519.dto.NodelistEntryDto;

import java.util.Map;

/**
 * Nodelist service layer interface
 */
public interface NodelistService {
    /**
     * Get nodelist entry
     *
     * @param zone zone
     * @param network network
     * @param node node address
     * @return Nodelist entry
     */
    NodelistEntryDto getNodelistEntry(int zone, int network, int node);

    /**
     * Get zone nodelist entries
     *
     * @param zone zone
     * @return List of nodelist entries
     */
    NodelistEntryDto getZoneNodelistEntries(int zone);

    /**
     * Get network nodelist entries
     *
     * @param zone zone
     * @param network network
     * @return List of nodelist entries
     */
    NodelistEntryDto getNetworkNodelistEntries(int zone, int network);

    /**
     * Get all nodelist entries
     *
     * @return Map of nodelist entries
     */
    Map<Integer, NodelistEntryDto> getAllNodelistEntries();
}
