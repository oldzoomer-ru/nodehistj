package ru.oldzoomer.nodehistj_newest_nodelists.controller;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

/**
 * Controller for working with current Fidonet nodelists (FTS-0005 standard).
 * <p>
 * Provides REST API endpoints for retrieving node information with various filtering capabilities:
 * <ul>
 *   <li>Get all nodes</li>
 *   <li>Filter by zone (1-6)</li>
 *   <li>Filter by zone and network</li>
 *   <li>Filter by specific node (zone+network+node)</li>
 * </ul>
 *
 * <p><b>Example requests:</b>
 * <pre>
 * GET /nodelist - get all nodes
 * GET /nodelist?zone=2 - get nodes in zone 2
 * GET /nodelist?zone=2&network=10 - get nodes in zone 2, network 10
 * GET /nodelist?zone=2&network=10&node=5 - get specific node
 * </pre>
 *
 * @see NodelistService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/nodelist")
@Validated
@Slf4j
public class NodelistController {
    private final NodelistService nodelistService;

    /**
     * Retrieves list of nodes with optional filtering by zone, network and node.
     * <p>
     * <b>Filtering options:</b>
     * <ul>
     *   <li>No parameters - returns all nodes</li>
     *   <li>Zone only - returns nodes in specified zone (1-6)</li>
     *   <li>Zone + network - returns nodes in specified network</li>
     *   <li>Zone + network + node - returns specific node</li>
     * </ul>
     *
     * <p><b>Response codes:</b>
     * <ul>
     *   <li>200 - Successful operation</li>
     *   <li>400 - Invalid parameters (validation failed)</li>
     *   <li>404 - Requested data not found</li>
     *   <li>500 - Internal server error</li>
     * </ul>
     *
     * @param zone Zone identifier (optional, 1-6 per Fidonet standard)
     * @param network Network identifier (optional, 1-32768)
     * @param node Node identifier (optional, 1-32768)
     * @return List of {@link NodeEntryDto} objects matching filter criteria
     * @throws jakarta.validation.ConstraintViolationException if validation fails (400)
     * @throws IllegalArgumentException if invalid parameter combination (400)
     */
    @GetMapping("")
    @Cacheable(value = "nodelistRequests", key = "{#zone, #network, #node}")
    public List<NodeEntryDto> getNodelistEntry(
            @RequestParam(required = false) @Min(1) @Max(6) Integer zone,
            @RequestParam(required = false) @Min(1) @Max(32768) Integer network,
            @RequestParam(required = false) @Min(1) @Max(32768) Integer node) {
        
        if (zone != null && network == null && node != null) {
            log.warn("Invalid request: node specified without network");
            throw new IllegalArgumentException("Cannot specify node without network");
        }
        
        log.debug("Processing nodelist request - zone: {}, network: {}, node: {}", 
            zone, network, node);
        
        return zone == null ? nodelistService.getNodelistEntries()
                : network == null ? nodelistService.getNodelistEntry(zone)
                : node == null ? nodelistService.getNodelistEntry(zone, network)
                : List.of(nodelistService.getNodelistEntry(zone, network, node));
    }
}
