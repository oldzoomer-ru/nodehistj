package ru.oldzoomer.nodehistj_history_diff.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_history_diff.dto.ChangeSummaryDto;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.mapper.NodeHistoryEntryMapper;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

/**
 * Service implementation for node history operations.
 * Provides methods for retrieving and analyzing node changes history with
 * caching support.
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
     * @param zone     The zone number of the node.
     * @param network  The network number of the node.
     * @param node     The node number.
     * @param pageable Pagination information.
     * @return A page of node history entries.
     */
    @Override
    public List<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node,
            Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkAndNode(zone, network, node, pageable)
                .map(nodeHistoryEntryMapper::toDto)
                .toList();
    }

    /**
     * Retrieves history for a specific network with pagination.
     *
     * @param zone     The zone number of the network.
     * @param network  The network number.
     * @param pageable Pagination information.
     * @return A page of network history entries.
     */
    @Override
    public List<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetwork(zone, network, pageable)
                .map(nodeHistoryEntryMapper::toDto)
                .toList();
    }

    /**
     * Retrieves history for a specific zone with pagination.
     *
     * @param zone     The zone number.
     * @param pageable Pagination information.
     * @return A page of zone history entries.
     */
    @Override
    public List<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZone(zone, pageable)
                .map(nodeHistoryEntryMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all history entries with pagination.
     *
     * @param pageable Pagination information.
     * @return A page of all history entries.
     */
    @Override
    public List<NodeHistoryEntryDto> getAllHistory(Pageable pageable) {
        return nodeHistoryEntryRepository
                .findAll(pageable)
                .map(nodeHistoryEntryMapper::toDto)
                .toList();
    }

    /**
     * Get changes for a specific date
     *
     * @param date The date to filter changes (format: yyyy-MM-dd)
     * @return List of changes for the specified date
     */
    @Override
    @Cacheable("changesForDate")
    public List<NodeHistoryEntryDto> getChangesForDate(String date) {
        LocalDate targetDate = LocalDate.parse(date);
        return nodeHistoryEntryRepository.findAll().stream()
                .filter(entry -> entry.getChangeDate() != null
                        && entry.getChangeDate().equals(targetDate))
                .map(nodeHistoryEntryMapper::toDto)
                .toList();
    }

    /**
     * Get changes by type
     *
     * @param changeType The type of change to filter (ADDED, MODIFIED, REMOVED)
     * @return List of changes for the specified type
     */
    @Override
    @Cacheable("changesByType")
    public List<NodeHistoryEntryDto> getChangesByType(String changeType) {
        try {
            NodeHistoryEntry.ChangeType type = NodeHistoryEntry.ChangeType
                    .valueOf(changeType.toUpperCase());
            return nodeHistoryEntryRepository.findAll().stream()
                    .filter(entry -> entry.getChangeType() == type)
                    .map(nodeHistoryEntryMapper::toDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid change type: " + changeType +
                    ". Valid types are: ADDED, MODIFIED, REMOVED");
        }
    }

    /**
     * Get change summary for date range
     *
     * @param startDate Start date of the range (format: yyyy-MM-dd)
     * @param endDate   End date of the range (format: yyyy-MM-dd)
     * @return List of change summary entries
     */
    @Override
    @Cacheable("changeSummary")
    public List<ChangeSummaryDto> getChangeSummary(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Group changes by date and count by type
        Map<LocalDate, Map<NodeHistoryEntry.ChangeType, Long>> changesByDate = nodeHistoryEntryRepository
                .findAll().stream()
                .filter(entry -> entry.getChangeDate() != null &&
                        !entry.getChangeDate().isBefore(start) &&
                        !entry.getChangeDate().isAfter(end))
                .collect(Collectors.groupingBy(
                        NodeHistoryEntry::getChangeDate,
                        Collectors.groupingBy(NodeHistoryEntry::getChangeType,
                                Collectors.counting())));

        // Convert to ChangeSummaryDto
        return changesByDate.entrySet().stream()
                .map(entry -> {
                    Map<NodeHistoryEntry.ChangeType, Long> counts = entry.getValue();
                    ChangeSummaryDto summary = ChangeSummaryDto.builder()
                            .date(entry.getKey().toString())
                            .addedCount(counts
                                    .getOrDefault(NodeHistoryEntry.ChangeType.ADDED,
                                            0L)
                                    .intValue())
                            .modifiedCount(counts.getOrDefault(
                                    NodeHistoryEntry.ChangeType.MODIFIED, 0L)
                                    .intValue())
                            .removedCount(counts.getOrDefault(
                                    NodeHistoryEntry.ChangeType.REMOVED, 0L)
                                    .intValue())
                            .build();
                    return summary;
                })
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .toList();
    }
}