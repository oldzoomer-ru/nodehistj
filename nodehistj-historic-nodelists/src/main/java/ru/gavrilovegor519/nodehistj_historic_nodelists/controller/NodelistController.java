package ru.gavrilovegor519.nodehistj_historic_nodelists.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /**
     * Get all nodelist entries
     * @return Map of nodelist entries
     */
    @GetMapping("/")
    public Map<Integer, NodelistEntryDto> getNodelist() {
        return nodelistService.getNodelistEntries();
    }

    /**
     * Get zone nodelist entry
     * @param zone zone
     * @return Zone nodelist entry
     */
    @GetMapping("/{zone}")
    public NodelistEntryDto getNodelist(@PathVariable int zone) {
        return nodelistService.getNodelistEntry(zone);
    }

    /**
     * Get network nodelist entry
     * @param zone zone
     * @param network network
     * @return Network nodelist entry
     */
    @GetMapping("/{zone}/{network}")
    public NodelistEntryDto getNodelist(@PathVariable int zone, @PathVariable int network) {
        return nodelistService.getNodelistEntry(zone, network);
    }

    /**
     * Get node nodelist entry
     * @param zone zone
     * @param network network
     * @param node node address
     * @return Node nodelist entry
     */
    @GetMapping("/{zone}/{network}/{node}")
    public NodelistEntryDto getNodelistEntry(@PathVariable int zone, @PathVariable int network, @PathVariable int node) {
        return nodelistService.getNodelistEntry(zone, network, node);
    }
}
