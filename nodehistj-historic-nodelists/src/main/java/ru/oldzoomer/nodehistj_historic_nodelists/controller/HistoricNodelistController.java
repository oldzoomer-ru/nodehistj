package ru.oldzoomer.nodehistj_historic_nodelists.controller;

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
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

/**
 * Controller for working with historic Fidonet nodelists (FTS-0005 standard).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/historicNodelist")
@Validated
@Slf4j
public class HistoricNodelistController {
    private final HistoricNodelistService historicNodelistService;

    @GetMapping("")
    @Cacheable(value = "historicNodelistRequests", key = "{#year, #dayOfYear, #zone, #network, #node}")
    public List<NodeEntryDto> getNodelistEntry(
            @RequestParam @Min(1900) @Max(2100) Integer year,
            @RequestParam @Min(1) @Max(366) Integer dayOfYear,
            @RequestParam(required = false) @Min(1) @Max(6) Integer zone,
            @RequestParam(required = false) @Min(1) @Max(32768) Integer network,
            @RequestParam(required = false) @Min(1) @Max(32768) Integer node) {

        log.debug("Processing historic nodelist request - year: {}, day: {}, zone: {}, network: {}, node: {}", 
            year, dayOfYear, zone, network, node);

        if (zone != null && network == null && node != null) {
            log.warn("Invalid request: node specified without network");
            throw new IllegalArgumentException("Cannot specify node without network");
        }

        return zone == null ? historicNodelistService.getNodelistEntries(year, dayOfYear)
                : network == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone)
                : node == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network)
                : List.of(historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network, node));
    }
}
