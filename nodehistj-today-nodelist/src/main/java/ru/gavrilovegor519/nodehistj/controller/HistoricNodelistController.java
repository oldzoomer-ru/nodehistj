package ru.gavrilovegor519.nodehistj.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gavrilovegor519.dto.NodelistEntryDto;
import ru.gavrilovegor519.nodehistj.service.HistoricNodelistService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historic-nodelist")
public class HistoricNodelistController {
    private final HistoricNodelistService historicNodelistService;

    @GetMapping("/{year}/{dayOfYear}")
    public Map<Integer, NodelistEntryDto> getNodelist(@PathVariable int year, @PathVariable int dayOfYear) {
        return historicNodelistService.getNodelistEntries(year, dayOfYear);
    }

    @GetMapping("/{year}/{dayOfYear}/{zone}")
    public NodelistEntryDto getNodelistEntry(@PathVariable int year, @PathVariable int dayOfYear,
                                             @PathVariable int zone) {
        return historicNodelistService.getNodelistEntry(year, dayOfYear, zone);
    }

    @GetMapping("/{year}/{dayOfYear}/{zone}/{network}")
    public NodelistEntryDto getNodelistEntry(@PathVariable int year, @PathVariable int dayOfYear,
                                             @PathVariable int zone, @PathVariable int network) {
        return historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network);
    }

    @GetMapping("/{year}/{dayOfYear}/{zone}/{network}/{node}")
    public NodelistEntryDto getNodelistEntry(@PathVariable int year, @PathVariable int dayOfYear,
                                             @PathVariable int zone, @PathVariable int network,
                                             @PathVariable int node) {
        return historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network, node);
    }
}

