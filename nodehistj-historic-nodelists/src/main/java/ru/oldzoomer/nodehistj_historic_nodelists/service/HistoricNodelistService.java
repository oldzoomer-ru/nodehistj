package ru.oldzoomer.nodehistj_historic_nodelists.service;

import java.time.Year;
import java.util.Set;

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
     * @return a set of NodeEntryDto objects representing the nodelist entries
     */
    Set<NodeEntryDto> getNodelistEntries(Year year, Integer dayOfYear);

    /**
     * Gets nodelist entries for a specific zone within a specific year and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    Set<NodeEntryDto> getNodelistEntry(Year year, Integer dayOfYear, Integer zone);

    /**
     * Gets nodelist entries for a specific network within a specific zone, year, and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    Set<NodeEntryDto> getNodelistEntry(Year year, Integer dayOfYear, Integer zone, Integer network);

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
    NodeEntryDto getNodelistEntry(Year year, Integer dayOfYear, Integer zone, Integer network, Integer node);
}
