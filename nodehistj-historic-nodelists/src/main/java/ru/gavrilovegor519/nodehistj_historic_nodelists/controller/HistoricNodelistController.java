package ru.gavrilovegor519.nodehistj_historic_nodelists.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodelistDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.HistoricNodelistService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historicNodelist")
public class HistoricNodelistController {
    private final HistoricNodelistService historicNodelistService;

    @GetMapping("")
    public List<NodelistDto> getNodelistEntry(@RequestParam Integer year,
                                              @RequestParam Integer dayOfYear,
                                              @RequestParam(required = false) Integer zone,
                                              @RequestParam(required = false) Integer network,
                                              @RequestParam(required = false) Integer node) {
        return zone == null ? historicNodelistService.getNodelistEntries(year, dayOfYear)
                : network == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone)
                : node == null ? historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network)
                : List.of(historicNodelistService.getNodelistEntry(year, dayOfYear, zone, network, node));
    }
}

