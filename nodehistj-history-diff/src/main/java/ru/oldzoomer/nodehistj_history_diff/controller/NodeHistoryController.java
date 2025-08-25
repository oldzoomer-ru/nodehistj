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
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
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
    @Cacheable(value = "nodeHistory", unless = "#result == null || #result.isEmpty()")
    public List<NodeHistoryEntryDto> getNodeHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @PathVariable @Min(0) @Max(32767) Integer node,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNodeHistory(zone, network, node, pageable);
    }

    @GetMapping("/network/{zone}/{network}")
    @Cacheable(value = "networkHistory", unless = "#result == null || #result.isEmpty()")
    public List<NodeHistoryEntryDto> getNetworkHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @PathVariable @Min(1) @Max(32767) Integer network,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getNetworkHistory(zone, network, pageable);
    }

    @GetMapping("/zone/{zone}")
    @Cacheable(value = "zoneHistory", unless = "#result == null || #result.isEmpty()")
    public List<NodeHistoryEntryDto> getZoneHistory(
            @PathVariable @Min(1) @Max(32767) Integer zone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getZoneHistory(zone, pageable);
    }

    @GetMapping("/all")
    @Cacheable(value = "globalHistory", unless = "#result == null || #result.isEmpty()")
    public List<NodeHistoryEntryDto> getAllHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return nodeHistoryService.getAllHistory(pageable);
    }

    @GetMapping("/type/{changeType}")
    @Cacheable(value = "typeChanges", unless = "#result == null || #result.isEmpty()")
    public List<NodeHistoryEntryDto> getChangesByType(@PathVariable String changeType) {
        return nodeHistoryService.getChangesByType(changeType);
    }
}