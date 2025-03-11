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
     * Get all nodelist entries
     *
     * @return Map of nodelist entries
     */
    @Override
    @Cacheable(value = "allDataOfNodelist")
    public Map<Integer, NodelistEntryDto> getNodelistEntries() {
        return nodelist.getNodelistEntries();
    }

    /**
     * Get zone nodelist entry
     *
     * @param zone zone
     * @return Zone nodelist entry
     */
    @Override
    @Cacheable(value = "zoneNodelistEntry", key = "#zone")
    public NodelistEntryDto getNodelistEntry(int zone) {
        return nodelist.getZoneNodelistEntries(zone);
    }

    /**
     * Get network nodelist entry
     *
     * @param zone    zone
     * @param network network
     * @return Network nodelist entry
     */
    @Override
    @Cacheable(value = "networkNodelistEntry", key = "#zone + '-' + #network")
    public NodelistEntryDto getNodelistEntry(int zone, int network) {
        return nodelist.getNetworkNodelistEntries(zone, network);
    }

    /**
     * Get node nodelist entry
     *
     * @param zone zone
     * @param network network
     * @param node node address
     * @return Node nodelist entry
     */
    @Override
    @Cacheable(value = "nodeNodelistEntry", key = "#zone + '-' + #network + '-' + #node")
    public NodelistEntryDto getNodelistEntry(int zone, int network, int node) {
        return nodelist.getNodelistEntry(zone, network, node);
    }
}
