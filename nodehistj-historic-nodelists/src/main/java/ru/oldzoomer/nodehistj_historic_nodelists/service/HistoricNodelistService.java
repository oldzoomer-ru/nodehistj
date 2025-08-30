package ru.oldzoomer.nodehistj_historic_nodelists.service;

import java.util.List;

import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;

/**
 * Service interface for managing historic nodelist data.
 * <p>
 * Provides methods for retrieving historic nodelist entries with various filtering options:
 * <ul>
 *   <li>Get all entries for a specific date</li>
 *   <li>Filter by zone</li>
 *   <li>Filter by zone and network</li>
 *   <li>Get specific node entry</li>
 * </ul>
 * <p>
 * This interface defines the contract for historic nodelist operations, allowing for
 * implementation flexibility while maintaining consistent behavior across different
 * service implementations.
 */
public interface HistoricNodelistService {
    /**
     * Get all nodelist entries for a specific date.
     *
     * @param year      year of the nodelist
     * @param dayOfYear day of year (1-366)
     * @return List of all nodelist entries for the specified date
     */
    List<NodeEntryDto> getNodelistEntries(int year, int dayOfYear);

    /**
     * Get all nodelist entries for a specific zone on a specific date.
     *
     * @param year      year of the nodelist
     * @param dayOfYear day of year (1-366)
     * @param zone      zone identifier
     * @return List of nodelist entries for the specified zone and date
     */
    List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone);

    /**
     * Get all nodelist entries for a specific network within a zone on a specific date.
     *
     * @param year      year of the nodelist
     * @param dayOfYear day of year (1-366)
     * @param zone      zone identifier
     * @param network   network identifier
     * @return List of nodelist entries for the specified network, zone, and date
     */
    List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone, int network);

    /**
     * Get a specific node entry from the historic nodelist.
     *
     * @param year      year of the nodelist
     * @param dayOfYear day of year (1-366)
     * @param zone      zone identifier
     * @param network   network identifier
     * @param node      node address
     * @return NodeEntryDto containing details of the specified node
     */
    NodeEntryDto getNodelistEntry(int year, int dayOfYear, int zone, int network, int node);
}
