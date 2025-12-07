package ru.oldzoomer.nodehistj_history_diff.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

import java.util.List;

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
}