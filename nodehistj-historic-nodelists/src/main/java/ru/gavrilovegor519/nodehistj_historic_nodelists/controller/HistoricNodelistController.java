package ru.gavrilovegor519.nodehistj_historic_nodelists.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gavrilovegor519.dto.NodelistEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.HistoricNodelistService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historicNodelist")
public class HistoricNodelistController {
    private final HistoricNodelistService historicNodelistService;

    @GetMapping("")
    public Map<Integer, NodelistEntryDto> getNodelist(@RequestParam Integer year, @RequestParam Integer dayOfYear) {
        return historicNodelistService.getNodelistEntries(year, dayOfYear);
    }

    @GetMapping("/getEntry")
    public NodelistEntryDto getNodelistEntry(@RequestParam Integer year, @RequestParam Integer dayOfYear,
                                             @RequestParam Integer zone, @RequestParam(required = false) Integer network,
                                             @RequestParam(required = false) Integer node) {
        return network == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone)
                : node == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network)
                : historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network, node);
    }
}

