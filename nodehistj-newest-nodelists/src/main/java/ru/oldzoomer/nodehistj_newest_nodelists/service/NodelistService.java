package ru.oldzoomer.nodehistj_newest_nodelists.service;

import java.util.List;

import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;

/**
 * Service interface for managing current nodelist data.
 * <p>
 * Provides methods for retrieving current nodelist entries with various filtering options:
 * <ul>
 *   <li>Get all entries</li>
 *   <li>Filter by zone</li>
 *   <li>Filter by zone and network</li>
 *   <li>Get specific node entry</li>
 * </ul>
 * <p>
 * This interface defines the contract for current nodelist operations, allowing for
 * implementation flexibility while maintaining consistent behavior across different
 * service implementations.
 */
public interface NodelistService {
    /**
     * Get all nodelist entries.
     *
     * @return List of all nodelist entries
     */
    List<NodeEntryDto> getNodelistEntries();

    /**
     * Get all nodelist entries for a specific zone.
     *
     * @param zone zone identifier
     * @return List of nodelist entries for the specified zone
     */
    List<NodeEntryDto> getNodelistEntry(int zone);

    /**
     * Get all nodelist entries for a specific network within a zone.
     *
     * @param zone    zone identifier
     * @param network network identifier
     * @return List of nodelist entries for the specified network and zone
     */
    List<NodeEntryDto> getNodelistEntry(int zone, int network);

    /**
     * Get a specific node entry from the nodelist.
     *
     * @param zone    zone identifier
     * @param network network identifier
     * @param node    node address
     * @return NodeEntryDto containing details of the specified node
     */
    NodeEntryDto getNodelistEntry(int zone, int network, int node);
}
