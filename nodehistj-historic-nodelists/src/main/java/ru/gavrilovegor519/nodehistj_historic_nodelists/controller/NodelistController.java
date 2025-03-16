package ru.gavrilovegor519.nodehistj_historic_nodelists.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gavrilovegor519.dto.NodelistEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.NodelistService;

import java.util.Map;

/**
 * Nodelist controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/nodelist")
public class NodelistController {
    private final NodelistService nodelistService;

    @GetMapping("")
    public Map<Integer, NodelistEntryDto> getNodelist() {
        return nodelistService.getNodelistEntries();
    }

    @GetMapping("/getEntry")
    public NodelistEntryDto getNodelistEntry(@RequestParam Integer zone, @RequestParam(required = false) Integer network,
                                             @RequestParam(required = false) Integer node) {
        return network == null ? nodelistService.getNodelistEntry(zone)
                : node == null ? nodelistService.getNodelistEntry(zone, network)
                : nodelistService.getNodelistEntry(zone, network, node);
    }
}
