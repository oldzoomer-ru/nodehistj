package ru.oldzoomer.nodehistj_historic_nodelists.service;

import java.util.List;

import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;

/**
 * Historic nodelist service layer interface.
 * Provides methods for retrieving nodelist entries based on various criteria.
 */
public interface HistoricNodelistService {
    /**
     * Gets all nodelist entries for a specific year and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @return a list of NodeEntryDto objects representing the nodelist entries
     */
    List<NodeEntryDto> getNodelistEntries(int year, int dayOfYear);

    /**
     * Gets nodelist entries for a specific zone within a specific year and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @return a list of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone);

    /**
     * Gets nodelist entries for a specific network within a specific zone, year, and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a list of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone, int network);

    /**
     * Gets a specific nodelist entry for a node within a specific network, zone, year, and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entry
     * @param network the network of the nodelist entry
     * @param node the node address of the nodelist entry
     * @return a NodeEntryDto object representing the specific nodelist entry
     */
    NodeEntryDto getNodelistEntry(int year, int dayOfYear, int zone, int network, int node);
}
