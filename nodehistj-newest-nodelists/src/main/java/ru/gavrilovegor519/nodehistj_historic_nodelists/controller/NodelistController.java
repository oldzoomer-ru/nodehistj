package ru.gavrilovegor519.nodehistj_historic_nodelists.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.NodelistService;

import java.util.List;

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
