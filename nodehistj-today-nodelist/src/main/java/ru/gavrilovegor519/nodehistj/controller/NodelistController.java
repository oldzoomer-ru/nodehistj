package ru.gavrilovegor519.nodehistj.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.gavrilovegor519.dto.NodelistEntryDto;
import ru.gavrilovegor519.nodehistj.service.NodelistService;

import java.util.Map;

/**
 * Nodelist controller
 */
@RestController
@RequiredArgsConstructor
public class NodelistController {
    private final NodelistService nodelistService;

    /**
     * Get all nodelist entries
     * @return Map of nodelist entries
     */
    @GetMapping("/nodelist")
    public Map<Integer, NodelistEntryDto> getNodelist() {
        return nodelistService.getAllNodelistEntries();
    }

    /**
     * Get zone nodelist entries
     * @param zone zone
     * @return List of nodelist entries
     */
    @GetMapping("/nodelist/{zone}")
    public NodelistEntryDto getNodelist(@PathVariable int zone) {
        return nodelistService.getZoneNodelistEntries(zone);
    }

    /**
     * Get network nodelist entries
     * @param zone zone
     * @param network network
     * @return List of nodelist entries
     */
    @GetMapping("/nodelist/{zone}/{network}")
    public NodelistEntryDto getNodelist(@PathVariable int zone, @PathVariable int network) {
        return nodelistService.getNetworkNodelistEntries(zone, network);
    }

    /**
     * Get nodelist entry
     * @param zone zone
     * @param network network
     * @param node node address
     * @return Nodelist entry
     */
    @GetMapping("/nodelist/{zone}/{network}/{node}")
    public NodelistEntryDto getNodelistEntry(@PathVariable int zone, @PathVariable int network, @PathVariable int node) {
        return nodelistService.getNodelistEntry(zone, network, node);
    }
}
