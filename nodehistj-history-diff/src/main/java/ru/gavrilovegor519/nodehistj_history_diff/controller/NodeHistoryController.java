package ru.gavrilovegor519.nodehistj_history_diff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.gavrilovegor519.nodehistj_history_diff.dto.NodeChangeSummaryDto;
import ru.gavrilovegor519.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.gavrilovegor519.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.gavrilovegor519.nodehistj_history_diff.service.NodeHistoryService;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for node history and diff operations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class NodeHistoryController {
    
    private final NodeHistoryService nodeHistoryService;

    /**
     * Get history for a specific node
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
     * Get history for a specific network
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
     * Get history for a specific zone
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
     * Get all history entries
     */
    @GetMapping("/all")
    public Page<NodeHistoryEntryDto> getAllHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getAllHistory(pageable);
    }

    /**
     * Get changes for a specific date
     */
    @GetMapping("/date/{date}")
    public List<NodeHistoryEntryDto> getChangesForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return nodeHistoryService.getChangesForDate(date);
    }

    /**
     * Get changes between dates
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
     * Get changes by type
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
     * Get summary of changes for a date range
     */
    @GetMapping("/summary")
    public List<NodeChangeSummaryDto> getChangeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return nodeHistoryService.getChangeSummary(startDate, endDate);
    }

    /**
     * Get most active nodes (nodes with most changes)
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