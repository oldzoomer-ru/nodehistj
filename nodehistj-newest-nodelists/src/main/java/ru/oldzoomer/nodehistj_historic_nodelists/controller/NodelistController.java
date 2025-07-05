package ru.oldzoomer.nodehistj_historic_nodelists.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.service.NodelistService;

/**
 * Nodelist controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/nodelist")
public class NodelistController {
    private final NodelistService nodelistService;

    @GetMapping("")
    public List<NodeEntryDto> getNodelistEntry(@RequestParam(required = false) Integer zone,
                                               @RequestParam(required = false) Integer network,
                                               @RequestParam(required = false) Integer node) {
        return zone == null ? nodelistService.getNodelistEntries()
                : network == null ? nodelistService.getNodelistEntry(zone)
                : node == null ? nodelistService.getNodelistEntry(zone, network)
                : List.of(nodelistService.getNodelistEntry(zone, network, node));
    }
}
