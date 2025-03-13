package ru.gavrilovegor519.nodehistj.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.gavrilovegor519.Nodelist;
import ru.gavrilovegor519.dto.NodelistEntryDto;
import ru.gavrilovegor519.nodehistj.repo.NodelistEntityRepository;
import ru.gavrilovegor519.nodehistj.service.NodelistService;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NodelistServiceImpl implements NodelistService {
    private final NodelistEntityRepository nodelistEntityRepository;

    /**
     * Get all nodelist entries
     *
     * @return Map of nodelist entries
     */
    @Override
    @Cacheable(value = "allDataOfNodelist")
    public Map<Integer, NodelistEntryDto> getNodelistEntries() {
        return nodelistEntityRepository.findByNodelistYearAndNodelistName(
                Year.now().getValue(), String.format("nodelist.%03d", LocalDate.now().getDayOfYear())
        ).getNodelist();
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
        Nodelist nodelist = getNodelist();
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
        Nodelist nodelist = getNodelist();
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
        Nodelist nodelist = getNodelist();
        return nodelist.getNodelistEntry(zone, network, node);
    }

    private Nodelist getNodelist() {
        return new Nodelist(nodelistEntityRepository.findByNodelistYearAndNodelistName(
                Year.now().getValue(), String.format("nodelist.%03d", LocalDate.now().getDayOfYear())
        ).getNodelist());
    }
}
