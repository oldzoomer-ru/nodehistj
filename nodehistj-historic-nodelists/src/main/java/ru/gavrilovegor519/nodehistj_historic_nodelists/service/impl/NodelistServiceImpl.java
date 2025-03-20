package ru.gavrilovegor519.nodehistj_historic_nodelists.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.NodelistService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodelistServiceImpl implements NodelistService {
    private final NodeEntryRepository nodeEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    /**
     * Get all nodelist entries
     *
     * @return List of nodelist entries
     */
    @Override
    @Cacheable(value = "allDataOfNodelist")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntries() {
        return nodeEntryMapper.toDto(nodeEntryRepository.getAll());
    }

    /**
     * Get zone nodelist entry
     *
     * @param zone zone
     * @return Zone nodelist entry
     */
    @Override
    @Cacheable(value = "zoneNodelistEntry", key = "#zone")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int zone) {
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone));
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
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int zone, int network) {
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone, network));
    }

    /**
     * Get node nodelist entry
     *
     * @param zone    zone
     * @param network network
     * @param node    node address
     * @return Node nodelist entry
     */
    @Override
    @Cacheable(value = "nodeNodelistEntry", key = "#zone + '-' + #network + '-' + #node")
    @Transactional(readOnly = true)
    public NodeEntryDto getNodelistEntry(int zone, int network, int node) {
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone, network, node));
    }
}
