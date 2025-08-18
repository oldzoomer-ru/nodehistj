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
     * @param zone Zone ID (1-32767)
     * @param network Network ID (1-32767)
     * @param node Node ID (0-32767)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with node history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Node not found
     * @example Example request: GET /history/node/1/2/3?page=0&size=10
     */
    @GetMapping("/node/{zone}/{network}/{node}")
    @Cacheable(value = "nodeHistory")
    public List<Object> getNodeHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @PathVariable @Min(0) @Max(32767) Integer node,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNodeHistory(zone, network, node, pageable).toList();
    }

    /**
     * Get paginated history for a network
     * @param zone Zone ID (1-32767)
     * @param network Network ID (1-32767)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with network history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Network not found
     */
    @GetMapping("/network/{zone}/{network}")
    @Cacheable(value = "networkHistory")
    public List<Object> getNetworkHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNetworkHistory(zone, network, pageable).toList();
    }

    /**
     * Get paginated history for a zone
     * @param zone Zone ID (1-32767)
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with zone history
     * @response 200 OK - History retrieved successfully
     * @response 404 Not Found - Zone not found
     */
    @GetMapping("/zone/{zone}")
    @Cacheable(value = "zoneHistory")
    public List<Object> getZoneHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getZoneHistory(zone, pageable).toList();
    }

    /**
     * Get paginated history of all nodes
     * @param page Page number (0-based)
     * @param size Page size (default 20)
     * @return Page of NodeHistoryEntryDto with all history
     * @response 200 OK - History retrieved successfully
     */
    @GetMapping("/all")
    @Cacheable(value = "globalHistory")
    public List<Object> getAllHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getAllHistory(pageable).toList();
    }

    /**
     * Get all changes for a specific date
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return List of NodeHistoryEntryDto for the date
     * @response 200 OK - Changes retrieved successfully
     * @response 204 No Content - No changes for this date
     */
    @GetMapping("/date/{date}")
    @Cacheable(value = "dailyHistory", key = "#date")
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
    @Cacheable(value = "dateRangeHistory")
    public List<Object> getChangesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getChangesBetweenDates(startDate, endDate, pageable).toList();
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
    @Cacheable(value = "typeHistory")
    public List<Object> getChangesByType(
            @PathVariable NodeHistoryEntry.ChangeType changeType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getChangesByType(changeType, pageable).toList();
    }
}