package ru.oldzoomer.nodehistj_historic_nodelists.controller;

import java.time.Year;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

/**
 * Controller for working with historical Fidonet nodelists (FTS-0005 standard).
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
 * GET /historicNodelist?year=2024&dayOfYear=180 - get all nodes
 * GET /historicNodelist?year=2024&dayOfYear=180&zone=2 - get nodes in zone 2
 * GET /historicNodelist?year=2024&dayOfYear=180&zone=2&network=10 - get nodes in zone 2, network 10
 * GET /historicNodelistt?year=2024&dayOfYear=180&zone=2&network=10&node=5 - get specific node
 * </pre>
 *
 * @see HistoricNodelistService
 */
@RestController
@RequiredArgsConstructor
@Validated
@Log4j2
public class HistoricNodelistController {
    private final HistoricNodelistService historicNodelistService;

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
     * @param year Year of nodelist (required)
     * @param dayOfYear Day of year of nodelist (required)
     * @param zone Zone identifier (optional, 1-32767)
     * @param network Network identifier (optional, 1-32767)
     * @param node Node identifier (optional, 0-32767)
     * @return Set of {@link NodeEntryDto} objects matching filter criteria
     * @throws jakarta.validation.ConstraintViolationException if validation fails (400)
     * @throws IllegalArgumentException if invalid parameter combination (400)
     */
    @GetMapping("/historicNodelist")
    @Cacheable(value = "historicNodelistRequests", unless = "#result == null || #result.isEmpty()")
    public Set<NodeEntryDto> getNodelistEntry(
            @RequestParam Year year,
            @RequestParam @Min(1) @Max(366) Integer dayOfYear,
            @RequestParam(required = false) @Min(1) @Max(32767) Integer zone,
            @RequestParam(required = false) @Min(1) @Max(32767) Integer network,
            @RequestParam(required = false) @Min(0) @Max(32767) Integer node) {

        log.debug("Processing historic nodelist request - year: {}, day: {}, zone: {}, network: {}, node: {}",
            year, dayOfYear, zone, network, node);

        if (zone != null && network == null && node != null) {
            log.warn("Invalid request: node specified without network");
            throw new IllegalArgumentException("Cannot specify node without network");
        }

        return zone == null ? historicNodelistService.getNodelistEntries(year, dayOfYear)
                : network == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone)
                : node == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network)
                : historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network, node) != null
                ? Set.of(historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network, node))
                : Set.of();
    }
}
