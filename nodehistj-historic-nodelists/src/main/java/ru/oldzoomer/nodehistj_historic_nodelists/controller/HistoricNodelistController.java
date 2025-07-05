package ru.oldzoomer.nodehistj_historic_nodelists.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historicNodelist")
public class HistoricNodelistController {
    private final HistoricNodelistService historicNodelistService;

    @GetMapping("")
    public List<NodeEntryDto> getNodelistEntry(@RequestParam Integer year,
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

