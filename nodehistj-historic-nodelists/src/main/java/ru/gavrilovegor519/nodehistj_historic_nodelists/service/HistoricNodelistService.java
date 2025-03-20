package ru.gavrilovegor519.nodehistj_historic_nodelists.service;

import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodeEntryDto;

import java.util.List;

/**
 * Historic nodelist service layer interface
 */
public interface HistoricNodelistService {
    /**
     * Get all nodelist entries
     *
     * @param year      year
     * @param dayOfYear day of year
     * @return List of nodelist entries
     */
    List<NodeEntryDto> getNodelistEntries(int year, int dayOfYear);

    /**
     * Get zone nodelist entry
     *
     * @param year      year
     * @param dayOfYear day of year
     * @param zone      zone
     * @return Zone nodelist entry
     */
    List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone);

    /**
     * Get network nodelist entry
     *
     * @param year      year
     * @param dayOfYear day of year
     * @param zone      zone
     * @param network   network
     * @return Network nodelist entry
     */
    List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone, int network);

    /**
     * Get node nodelist entry
     *
     * @param year      year
     * @param dayOfYear day of year
     * @param zone      zone
     * @param network   network
     * @param node      node address
     * @return Node nodelist entry
     */
    NodeEntryDto getNodelistEntry(int year, int dayOfYear, int zone, int network, int node);
}
