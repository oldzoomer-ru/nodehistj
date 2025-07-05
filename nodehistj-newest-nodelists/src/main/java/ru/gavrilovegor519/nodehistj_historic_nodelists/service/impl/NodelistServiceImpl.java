package ru.gavrilovegor519.nodehistj_historic_nodelists.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.NodelistService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodelistServiceImpl implements NodelistService {
    private final NodeEntryRepository nodelistEntryRepository;
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
        log.debug("Fetching all nodelist entries");
        return nodeEntryMapper.toDto(nodelistEntryRepository.findAll());
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
        log.debug("Fetching nodelist entries for zone: {}", zone);
        return nodeEntryMapper.toDto(nodelistEntryRepository.getLast(zone));
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
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        return nodeEntryMapper.toDto(nodelistEntryRepository.getLast(zone, network));
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
        log.debug("Fetching nodelist entry for zone: {}, network: {}, node: {}", zone, network, node);
        return nodeEntryMapper.toDto(nodelistEntryRepository.getLast(zone, network, node));
    }
}
