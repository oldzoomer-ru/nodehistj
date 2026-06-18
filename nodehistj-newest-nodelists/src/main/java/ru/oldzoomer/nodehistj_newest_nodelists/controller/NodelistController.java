package ru.oldzoomer.nodehistj_newest_nodelists.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.Comparator;
import java.util.List;
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
    public ResponseEntity<List<NodeEntryDto>> getNodelistEntry(
            @RequestParam(required = false) @Min(1) @Max(32767) Integer zone,
            @RequestParam(required = false) @Min(1) @Max(32767) Integer network,
            @RequestParam(required = false) @Min(0) @Max(32767) Integer node,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "1000") @Min(1) @Max(10000) Integer size) {

        Set<NodeEntryDto> result;
        if (node != null && network != null && zone != null) {
            var optional = nodelistService.getNodelistEntry(zone, network, node);
            result = optional.map(Set::of).orElseGet(Set::of);
        } else if (network != null && zone != null) {
            result = nodelistService.getNodelistEntry(zone, network);
        } else if (zone != null) {
            result = nodelistService.getNodelistEntry(zone);
        } else {
            result = nodelistService.getNodelistEntries();
        }

        Pageable pageable = PageRequest.of(page, size);
        List<NodeEntryDto> paginatedEntries = result.stream()
            .sorted(Comparator.comparing(NodeEntryDto::zone)
                .thenComparing(NodeEntryDto::network)
                .thenComparing(NodeEntryDto::node))
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(result.size()))
            .body(paginatedEntries);
    }
}
