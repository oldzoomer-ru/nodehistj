package ru.oldzoomer.nodehistj_history_diff.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeChangeSummaryDto;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

/**
 * Controller for node history and diff operations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class NodeHistoryController {
    
    /** Service for node history operations */
    private final NodeHistoryService nodeHistoryService;

    /**
     * Get paginated history for a specific node
     * @param zone Zone ID (1-255)
     * @param network Network ID (1-65535)
     * @param node Node ID (1-65535)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with node history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Node not found
     * @example Example request: GET /history/node/1/2/3?page=0&size=10
     */
    @GetMapping("/node/{zone}/{network}/{node}")
    public Page<NodeHistoryEntryDto> getNodeHistory(
            @PathVariable Integer zone,
            @PathVariable Integer network,
            @PathVariable Integer node,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNodeHistory(zone, network, node, pageable);
    }

    /**
     * Get paginated history for a network
     * @param zone Zone ID (1-255)
     * @param network Network ID (1-65535)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with network history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Network not found
     */
    @GetMapping("/network/{zone}/{network}")
    public Page<NodeHistoryEntryDto> getNetworkHistory(
            @PathVariable Integer zone,
            @PathVariable Integer network,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNetworkHistory(zone, network, pageable);
    }

    /**
     * Get paginated history for a zone
     * @param zone Zone ID (1-255)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with zone history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Zone not found
     */
    @GetMapping("/zone/{zone}")
    public Page<NodeHistoryEntryDto> getZoneHistory(
            @PathVariable Integer zone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getZoneHistory(zone, pageable);
    }

    /**
     * Get paginated history of all nodes
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with all history
     * @response 200 OK - History retrieved successfully
     */
    @GetMapping("/all")
    public Page<NodeHistoryEntryDto> getAllHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getAllHistory(pageable);
    }

    /**
     * Get all changes for a specific date
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return List of NodeHistoryEntryDto for the date
     * @response 200 OK - Changes retrieved successfully
     * @response 204 No Content - No changes for this date
     */
    @GetMapping("/date/{date}")
    public List<NodeHistoryEntryDto> getChangesForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return nodeHistoryService.getChangesForDate(date);
    }

    /**
     * Get paginated changes between dates
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto for date range
     * @response 200 OK - Changes retrieved successfully
     * @response 400 Bad Request - Invalid date range
     */
    @GetMapping("/range")
    public Page<NodeHistoryEntryDto> getChangesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getChangesBetweenDates(startDate, endDate, pageable);
    }

    /**
     * Get paginated changes by change type
     * @param changeType Type of change (ADDED, REMOVED, MODIFIED)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto filtered by change type
     * @response 200 OK - Changes retrieved successfully
     * @response 400 Bad Request - Invalid change type
     */
    @GetMapping("/type/{changeType}")
    public Page<NodeHistoryEntryDto> getChangesByType(
            @PathVariable NodeHistoryEntry.ChangeType changeType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getChangesByType(changeType, pageable);
    }

    /**
     * Get summary statistics of changes for a date range
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return List of NodeChangeSummaryDto with change statistics
     * @response 200 OK - Summary retrieved successfully
     * @response 400 Bad Request - Invalid date range
     */
    @GetMapping("/summary")
    public List<NodeChangeSummaryDto> getChangeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return nodeHistoryService.getChangeSummary(startDate, endDate);
    }

    /**
     * Get most active nodes (nodes with most changes) in period
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @param page Page number (0-based)
     * @param size Page size (default 10)
     * @return List of Object arrays with node IDs and change counts
     * @response 200 OK - Active nodes retrieved successfully
     * @response 400 Bad Request - Invalid date range
     */
    @GetMapping("/active-nodes")
    public List<Object[]> getMostActiveNodes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getMostActiveNodes(startDate, endDate, pageable);
    }
}