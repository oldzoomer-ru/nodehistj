package ru.oldzoomer.nodehistj_history_diff.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
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
public class NodeHistoryServiceImpl implements NodeHistoryService {
    
    /** Repository for accessing node history entries */
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;
    
    /** Mapper for converting between entity and DTO */
    private final NodeHistoryEntryMapper nodeHistoryEntryMapper;

    /**
     * Retrieves history for a specific node with pagination.
     *
     * @param zone The zone number of the node.
     * @param network The network number of the node.
     * @param node The node number.
     * @param pageable Pagination information.
     * @return A page of node history entries.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkAndNodeOrderByChangeDateDesc(zone, network, node, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves history for a specific network with pagination.
     *
     * @param zone The zone number of the network.
     * @param network The network number.
     * @param pageable Pagination information.
     * @return A page of network history entries.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkOrderByChangeDateDescNodeAsc(zone, network, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves history for a specific zone with pagination.
     *
     * @param zone The zone number.
     * @param pageable Pagination information.
     * @return A page of zone history entries.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneOrderByChangeDateDescNetworkAscNodeAsc(zone, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves all history entries with pagination.
     *
     * @param pageable Pagination information.
     * @return A page of all history entries.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getAllHistory(Pageable pageable) {
        return nodeHistoryEntryRepository
                .findAllByOrderByChangeDateDescZoneAscNetworkAscNodeAsc(pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves all changes for a specific date.
     *
     * @param date The date to filter changes.
     * @return A list of changes for the specified date.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NodeHistoryEntryDto> getChangesForDate(LocalDate date) {
        return nodeHistoryEntryMapper.toDto(
                nodeHistoryEntryRepository.findByChangeDateOrderByZoneAscNetworkAscNodeAsc(date));
    }

    /**
     * Retrieves changes between two dates with pagination.
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @param pageable Pagination information.
     * @return A page of changes in the date range.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getChangesBetweenDates(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByChangeDateBetweenOrderByChangeDateDescZoneAscNetworkAscNodeAsc(startDate, endDate, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves changes filtered by change type with pagination.
     *
     * @param changeType The type of change to filter.
     * @param pageable Pagination information.
     * @return A page of changes of the specified type.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getChangesByType(NodeHistoryEntry.ChangeType changeType, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByChangeTypeOrderByChangeDateDescZoneAscNetworkAscNodeAsc(changeType, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Gets summary statistics of changes between dates.
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return A list of change summary statistics.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NodeChangeSummaryDto> getChangeSummary(LocalDate startDate, LocalDate endDate) {
        return nodeHistoryEntryRepository.getChangeSummary(startDate, endDate);
    }

    /**
     * Gets a list of most frequently changed nodes.
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @param pageable Pagination information.
     * @return A list of active nodes with change counts.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMostActiveNodes(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return nodeHistoryEntryRepository.getMostActiveNodes(startDate, endDate, pageable);
    }
}