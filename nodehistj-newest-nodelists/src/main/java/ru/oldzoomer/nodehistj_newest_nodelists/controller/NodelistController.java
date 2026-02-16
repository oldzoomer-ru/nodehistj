package ru.oldzoomer.nodehistj_newest_nodelists.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.Set;

/**
 * Controller for working with current Fidonet nodelists (FTS-0005 standard).
 * <p>
 * Provides REST API endpoints for retrieving node information with various filtering capabilities:
 * <ul>
 *   <li>Get all nodes</li>
 *   <li>Filter by zone</li>
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
@Validated
@RequestMapping("/nodelist")
public class NodelistController {
    private final NodelistService nodelistService;

    /**
     * Retrieves list of nodes with optional filtering by zone, network and node.
     * <p>
     * <b>Filtering options:</b>
     * <ul>
     *   <li>No parameters - returns all nodes</li>
     *   <li>Zone only - returns nodes in specified zone</li>
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
     * @param zone Zone identifier (optional, 1-32767)
     * @param network Network identifier (optional, 1-32767)
     * @param node Node identifier (optional, 0-32767)
     * @return Set of {@link NodeEntryDto} objects matching filter criteria
     * @throws jakarta.validation.ConstraintViolationException if validation fails (400)
     * @throws IllegalArgumentException if invalid parameter combination (400)
     */
    @GetMapping
    @Cacheable(value = "nodelistRequests", unless = "#result == null || #result.isEmpty()")
    public Set<NodeEntryDto> getNodelistEntry(
            @RequestParam(required = false) @Min(1) @Max(32767) Integer zone,
            @RequestParam(required = false) @Min(1) @Max(32767) Integer network,
            @RequestParam(required = false) @Min(0) @Max(32767) Integer node) {

        if (node != null && network != null && zone != null) {
            NodeEntryDto result = nodelistService.getNodelistEntry(zone, network, node);
            return result != null ? Set.of(result) : Set.of();
        } else if (network != null && zone != null) {
            return nodelistService.getNodelistEntry(zone, network);
        } else if (zone != null) {
            return nodelistService.getNodelistEntry(zone);
        } else {
            return nodelistService.getNodelistEntries();
        }
    }
}
