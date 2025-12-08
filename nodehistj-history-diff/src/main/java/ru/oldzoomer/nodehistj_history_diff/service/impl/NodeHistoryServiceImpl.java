package ru.oldzoomer.nodehistj_history_diff.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.mapper.NodeHistoryEntryMapper;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

/**
 * Service implementation for node history operations.
 * Provides methods for retrieving and analyzing node changes history with caching support.
 * <p>
 * This service includes methods for:
 * - Retrieving history for specific nodes, networks, or zones
 * - Getting changes for specific dates or date ranges
 * - Filtering changes by type
 * - Generating summary statistics and identifying most active nodes
 * <p>
 * All methods use caching to improve performance for repeated queries.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NodeHistoryServiceImpl implements NodeHistoryService {

    /** Repository for accessing node history entries */
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;

    /** Mapper for converting between entity and DTO */
    private final NodeHistoryEntryMapper nodeHistoryEntryMapper;

    /**
     * Retrieves history for a specific node with pagination.
     *
     * @param zone     the zone number of the node
     * @param network  the network number of the node
     * @param node     the node number
     * @param pageable pagination information
     * @return a page of node history entries
     */
    @Override
    public Slice<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkAndNodeOrderByChangeDateDesc(zone, network, node, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves history for a specific network with pagination.
     *
     * @param zone     the zone number of the network
     * @param network  the network number
     * @param pageable pagination information
     * @return a page of network history entries
     */
    @Override
    public Slice<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkOrderByChangeDateDescNodeAsc(zone, network, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves history for a specific zone with pagination.
     *
     * @param zone     the zone number
     * @param pageable pagination information
     * @return a page of zone history entries
     */
    @Override
    public Slice<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneOrderByChangeDateDescNetworkAscNodeAsc(zone, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves all history entries with pagination.
     *
     * @param pageable pagination information
     * @return a page of all history entries
     */
    @Override
    public Slice<NodeHistoryEntryDto> getAllHistory(Pageable pageable) {
        return nodeHistoryEntryRepository
                .findAllByOrderByChangeDateDescZoneAscNetworkAscNodeAsc(pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }
}