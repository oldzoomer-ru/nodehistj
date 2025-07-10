package ru.oldzoomer.nodehistj_newest_nodelists.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

/**
 * Controller for working with current nodelists.
 * <p>
 * Provides REST API for retrieving node information with filtering capabilities
 * by zone, network and specific node.
 *
 * @see NodelistService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/nodelist")
public class NodelistController {
    private final NodelistService nodelistService;

    /**
     * Retrieves list of nodes with filtering capabilities.
     * <p>
     * Supports three filtering levels:
     * 1. Zone only - returns all nodes in specified zone
     * 2. Zone + network - returns all nodes in specified network
     * 3. Zone + network + node - returns specific node
     *
     * @param zone zone identifier (optional)
     * @param network network identifier (optional)
     * @param node node identifier (optional)
     * @return list of node DTOs matching filter criteria
     */
    @GetMapping("")
    public List<NodeEntryDto> getNodelistEntry(@RequestParam(required = false) Integer zone,
                                               @RequestParam(required = false) Integer network,
                                               @RequestParam(required = false) Integer node) {
        return zone == null ? nodelistService.getNodelistEntries()
                : network == null ? nodelistService.getNodelistEntry(zone)
                : node == null ? nodelistService.getNodelistEntry(zone, network)
                : List.of(nodelistService.getNodelistEntry(zone, network, node));
    }
}
