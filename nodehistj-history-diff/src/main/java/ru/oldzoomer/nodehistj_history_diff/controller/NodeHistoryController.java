package ru.oldzoomer.nodehistj_history_diff.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

/**
 * Controller for node history and diff operations.
 * Provides endpoints for retrieving node history, network history, zone history, and other related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class NodeHistoryController {

    /** Service for node history operations */
    private final NodeHistoryService nodeHistoryService;

    /**
     * Gets paginated history for a specific node.
     *
     * @param zone the zone ID (1-32767)
     * @param network the network ID (1-32767)
     * @param node the node ID (0-32767)
     * @param page the page number (0-based)
     * @param size the page size (default 20)
     * @return a list of NodeHistoryEntryDto objects with node history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Node not found
     * @example Example request: GET /history/node/1/2/3?page=0&size=10
     */
    @GetMapping("/node/{zone}/{network}/{node}")
    @Cacheable("nodeHistory")
    public List<NodeHistoryEntryDto> getNodeHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @PathVariable @Min(0) @Max(32767) Integer node,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNodeHistory(zone, network, node, pageable).toList();
    }

    /**
     * Gets paginated history for a network.
     *
     * @param zone the zone ID (1-32767)
     * @param network the network ID (1-32767)
     * @param page the page number (0-based)
     * @param size the page size (default 20)
     * @return a list of NodeHistoryEntryDto objects with network history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Network not found
     */
    @GetMapping("/network/{zone}/{network}")
    @Cacheable("networkHistory")
    public List<NodeHistoryEntryDto> getNetworkHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNetworkHistory(zone, network, pageable).toList();
    }

    /**
     * Gets paginated history for a zone.
     *
     * @param zone the zone ID (1-32767)
     * @param page the page number (0-based)
     * @param size the page size (default 20)
     * @return a list of NodeHistoryEntryDto objects with zone history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Zone not found
     */
    @GetMapping("/zone/{zone}")
    @Cacheable("zoneHistory")
    public List<NodeHistoryEntryDto> getZoneHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getZoneHistory(zone, pageable).toList();
    }

    /**
     * Gets paginated history of all nodes.
     *
     * @param page the page number (0-based)
     * @param size the page size (default 20)
     * @return a list of NodeHistoryEntryDto objects with all history
     * @response 200 OK - History retrieved successfully
     */
    @GetMapping("/all")
    @Cacheable("globalHistory")
    public List<NodeHistoryEntryDto> getAllHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getAllHistory(pageable).toList();
    }

    /**
     * Gets all changes for a specific date.
     *
     * @param date the date in ISO format (YYYY-MM-DD)
     * @return a list of NodeHistoryEntryDto objects for the date
     * @response 200 OK - Changes retrieved successfully
     * @response 204 No Content - No changes for this date
     */
    @GetMapping("/date/{date}")
    @Cacheable("dailyHistory")
    public List<NodeHistoryEntryDto> getChangesForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return nodeHistoryService.getChangesForDate(date);
    }

    /**
     * Gets paginated changes between dates.
     *
     * @param startDate the start date in ISO format (YYYY-MM-DD)
     * @param endDate the end date in ISO format (YYYY-MM-DD)
     * @param page the page number (0-based)
     * @param size the page size (default 20)
     * @return a list of NodeHistoryEntryDto objects for the date range
     * @response 200 OK - Changes retrieved successfully
     * @response 400 Bad Request - Invalid date range
     */
    @GetMapping("/range")
    @Cacheable("dateRangeHistory")
    public List<NodeHistoryEntryDto> getChangesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getChangesBetweenDates(startDate, endDate, pageable).toList();
    }

    /**
     * Gets paginated changes by change type.
     *
     * @param changeType the type of change (ADDED, REMOVED, MODIFIED)
     * @param page the page number (0-based)
     * @param size the page size (default 20)
     * @return a list of NodeHistoryEntryDto objects filtered by change type
     * @response 200 OK - Changes retrieved successfully
     * @response 400 Bad Request - Invalid change type
     */
    @GetMapping("/type/{changeType}")
    @Cacheable("typeHistory")
    public List<NodeHistoryEntryDto> getChangesByType(
            @PathVariable NodeHistoryEntry.ChangeType changeType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getChangesByType(changeType, pageable).toList();
    }

    /**
     * Gets summary statistics of changes for a date range.
     *
     * @param startDate the start date in ISO format (YYYY-MM-DD)
     * @param endDate the end date in ISO format (YYYY-MM-DD)
     * @return a list of NodeChangeSummaryDto objects with change statistics
     * @response 200 OK - Summary retrieved successfully
     * @response 400 Bad Request - Invalid date range
     */
    @GetMapping("/summary")
    @Cacheable("changeSummary")
    public List<NodeChangeSummaryDto> getChangeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return nodeHistoryService.getChangeSummary(startDate, endDate);
    }

    /**
     * Gets most active nodes (nodes with most changes) in a period.
     *
     * @param startDate the start date in ISO format (YYYY-MM-DD)
     * @param endDate the end date in ISO format (YYYY-MM-DD)
     * @param page the page number (0-based)
     * @param size the page size (default 10)
     * @return a list of Object arrays with node IDs and change counts
     * @response 200 OK - Active nodes retrieved successfully
     * @response 400 Bad Request - Invalid date range
     */
    @GetMapping("/active-nodes")
    @Cacheable("activeNodes")
    public List<Object[]> getMostActiveNodes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getMostActiveNodes(startDate, endDate, pageable);
    }
}