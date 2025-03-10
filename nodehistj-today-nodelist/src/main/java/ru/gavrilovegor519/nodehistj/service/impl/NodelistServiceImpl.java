package ru.gavrilovegor519.nodehistj.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.gavrilovegor519.Nodelist;
import ru.gavrilovegor519.dto.NodelistEntryDto;
import ru.gavrilovegor519.nodehistj.service.NodelistService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NodelistServiceImpl implements NodelistService {
    private final Nodelist nodelist;

    /**
     * Get nodelist entry
     *
     * @param zone zone
     * @param network network
     * @param node node address
     * @return Nodelist entry
     */
    @Override
    @Cacheable(value = "nodelist", key = "#zone + '-' + #network + '-' + #node")
    public NodelistEntryDto getNodelistEntry(int zone, int network, int node) {
        return nodelist.getNodelistEntry(zone, network, node);
    }

    /**
     * Get zone nodelist entries
     *
     * @param zone zone
     * @return List of nodelist entries
     */
    @Override
    @Cacheable(value = "nodelist", key = "#zone")
    public NodelistEntryDto getZoneNodelistEntries(int zone) {
        return nodelist.getZoneNodelistEntries(zone);
    }

    /**
     * Get network nodelist entries
     *
     * @param zone    zone
     * @param network network
     * @return List of nodelist entries
     */
    @Override
    @Cacheable(value = "nodelist", key = "#zone + '-' + #network")
    public NodelistEntryDto getNetworkNodelistEntries(int zone, int network) {
        return nodelist.getNetworkNodelistEntries(zone, network);
    }

    /**
     * Get all nodelist entries
     *
     * @return Map of nodelist entries
     */
    @Override
    @Cacheable(value = "nodelist")
    public Map<Integer, NodelistEntryDto> getAllNodelistEntries() {
        return nodelist.getNodelistEntries();
    }
}
