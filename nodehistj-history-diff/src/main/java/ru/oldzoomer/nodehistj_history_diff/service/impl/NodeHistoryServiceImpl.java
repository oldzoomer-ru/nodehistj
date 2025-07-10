package ru.oldzoomer.nodehistj_history_diff.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
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
 */
@Service
@RequiredArgsConstructor
public class NodeHistoryServiceImpl implements NodeHistoryService {
    
    /** Repository for accessing node history entries */
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;
    
    /** Mapper for converting between entity and DTO */
    private final NodeHistoryEntryMapper nodeHistoryEntryMapper;

    /**
     * Retrieves history for a specific node with pagination
     * @param zone node zone number
     * @param network node network number
     * @param node node number
     * @param pageable pagination information
     * @return page of node history entries
     */
    @Override
    @Cacheable(value = "nodeHistory", key = "#zone + '-' + #network + '-' + #node + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkAndNodeOrderByChangeDateDesc(zone, network, node, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves history for a specific network with pagination
     * @param zone network zone number
     * @param network network number
     * @param pageable pagination information
     * @return page of network history entries
     */
    @Override
    @Cacheable(value = "networkHistory", key = "#zone + '-' + #network + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkOrderByChangeDateDescNodeAsc(zone, network, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves history for a specific zone with pagination
     * @param zone zone number
     * @param pageable pagination information
     * @return page of zone history entries
     */
    @Override
    @Cacheable(value = "zoneHistory", key = "#zone + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneOrderByChangeDateDescNetworkAscNodeAsc(zone, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves all history entries with pagination
     * @param pageable pagination information
     * @return page of all history entries
     */
    @Override
    @Cacheable(value = "allHistory", key = "#pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getAllHistory(Pageable pageable) {
        return nodeHistoryEntryRepository
                .findAllByOrderByChangeDateDescZoneAscNetworkAscNodeAsc(pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves all changes for a specific date
     * @param date date to filter changes
     * @return list of changes for the specified date
     */
    @Override
    @Cacheable(value = "changesForDate", key = "#date")
    @Transactional(readOnly = true)
    public List<NodeHistoryEntryDto> getChangesForDate(LocalDate date) {
        return nodeHistoryEntryMapper.toDto(
                nodeHistoryEntryRepository.findByChangeDateOrderByZoneAscNetworkAscNodeAsc(date));
    }

    /**
     * Retrieves changes between two dates with pagination
     * @param startDate start date of range
     * @param endDate end date of range
     * @param pageable pagination information
     * @return page of changes in the date range
     */
    @Override
    @Cacheable(value = "changesBetweenDates", key = "#startDate + '-' + #endDate + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getChangesBetweenDates(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByChangeDateBetweenOrderByChangeDateDescZoneAscNetworkAscNodeAsc(startDate, endDate, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Retrieves changes filtered by change type with pagination
     * @param changeType type of change to filter
     * @param pageable pagination information
     * @return page of changes of specified type
     */
    @Override
    @Cacheable(value = "changesByType", key = "#changeType + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getChangesByType(NodeHistoryEntry.ChangeType changeType, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByChangeTypeOrderByChangeDateDescZoneAscNetworkAscNodeAsc(changeType, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    /**
     * Gets summary statistics of changes between dates
     * @param startDate start date of range
     * @param endDate end date of range
     * @return list of change summary statistics
     */
    @Override
    @Cacheable(value = "changeSummary", key = "#startDate + '-' + #endDate")
    @Transactional(readOnly = true)
    public List<NodeChangeSummaryDto> getChangeSummary(LocalDate startDate, LocalDate endDate) {
        return nodeHistoryEntryRepository.getChangeSummary(startDate, endDate);
    }

    /**
     * Gets list of most frequently changed nodes
     * @param startDate start date of range
     * @param endDate end date of range
     * @param pageable pagination information
     * @return list of active nodes with change counts
     */
    @Override
    @Cacheable(value = "mostActiveNodes", key = "#startDate + '-' + #endDate + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public List<Object[]> getMostActiveNodes(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return nodeHistoryEntryRepository.getMostActiveNodes(startDate, endDate, pageable);
    }
}