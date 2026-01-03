package ru.oldzoomer.nodehistj_history_diff.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
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
     * Unified endpoint to get history by optional zone, network, node.
     * Examples:
     *   GET /history?zone=1&network=2&node=3  -> node history
     *   GET /history?zone=1&network=2         -> network history
     *   GET /history?zone=1                   -> zone history
     *   GET /history                          -> all history
     */
    @GetMapping
    @Cacheable(value = "nodelistHistory", unless = "#result == null || #result.isEmpty()")
    public Page<NodeHistoryEntryDto> getHistory(
            @RequestParam(required = false) @Min(1) @Max(32767) Integer zone,
            @RequestParam(required = false) @Min(1) @Max(32767) Integer network,
            @RequestParam(required = false) @Min(0) @Max(32767) Integer node,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("changeDate")));

        if (node != null && network != null && zone != null) {
            return nodeHistoryService.getNodeHistory(zone, network, node, pageable);
        } else if (network != null && zone != null) {
            return nodeHistoryService.getNetworkHistory(zone, network, pageable);
        } else if (zone != null) {
            return nodeHistoryService.getZoneHistory(zone, pageable);
        } else {
            return nodeHistoryService.getAllHistory(pageable);
        }
    }
}