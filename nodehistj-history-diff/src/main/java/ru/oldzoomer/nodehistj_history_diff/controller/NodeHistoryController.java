package ru.oldzoomer.nodehistj_history_diff.controller;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

/**
 * Controller for node history and diff operations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class NodeHistoryController {
    
    private final NodeHistoryService nodeHistoryService;

    @GetMapping("/node/{zone}/{network}/{node}")
    @Cacheable("nodeHistory")
    public List<Object> getNodeHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @PathVariable @Min(0) @Max(32767) Integer node,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNodeHistory(zone, network, node, pageable).toList();
    }

    @GetMapping("/network/{zone}/{network}")
    @Cacheable("networkHistory")
    public List<Object> getNetworkHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNetworkHistory(zone, network, pageable).toList();
    }

    @GetMapping("/zone/{zone}")
    @Cacheable("zoneHistory")
    public List<Object> getZoneHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getZoneHistory(zone, pageable).toList();
    }

    @GetMapping("/all")
    @Cacheable("globalHistory")
    public List<Object> getAllHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getAllHistory(pageable).toList();
    }
}