package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

/**
 * Implementation of service for working with current nodelists.
 * <p>
 * Provides methods for retrieving node data with support for:
 * - Result caching (Spring Cache)
 * - Transactional data access
 * - Operation logging
 *
 * @see NodelistService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NodelistServiceImpl implements NodelistService {
    private final NodeEntryRepository nodelistEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    /**
     * Retrieves all nodelist entries.
     * <p>
     * Results are cached in "allDataOfNodelist" cache.
     *
     * @return list of DTO objects for all nodes
     * @see org.springframework.cache.annotation.Cacheable
     */
    @Override
    @Cacheable(value = "allDataOfNodelist")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntries() {
        log.debug("Fetching all nodelist entries");
        return nodeEntryMapper.toDto(nodelistEntryRepository.findAll());
    }

    /**
     * Retrieves nodelist entries for specified zone.
     * <p>
     * Results are cached in "zoneNodelistEntry" cache with zone as key.
     *
     * @param zone zone identifier
     * @return list of DTO objects for nodes in specified zone
     * @see org.springframework.cache.annotation.Cacheable
     */
    @Override
    @Cacheable(value = "zoneNodelistEntry", key = "#zone")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int zone) {
        log.debug("Fetching nodelist entries for zone: {}", zone);
        return nodeEntryMapper.toDto(nodelistEntryRepository.getLast(zone));
    }

    /**
     * Retrieves nodelist entries for specified network in zone.
     * <p>
     * Results are cached in "networkNodelistEntry" cache with composite key "zone-network".
     *
     * @param zone zone identifier
     * @param network network identifier
     * @return list of DTO objects for nodes in specified network
     * @see org.springframework.cache.annotation.Cacheable
     */
    @Override
    @Cacheable(value = "networkNodelistEntry", key = "#zone + '-' + #network")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int zone, int network) {
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        return nodeEntryMapper.toDto(nodelistEntryRepository.getLast(zone, network));
    }

    /**
     * Retrieves specific nodelist entry.
     * <p>
     * Results are cached in "nodeNodelistEntry" cache with composite key "zone-network-node".
     *
     * @param zone zone identifier
     * @param network network identifier
     * @param node node identifier
     * @return DTO object of requested node
     * @see org.springframework.cache.annotation.Cacheable
     */
    @Override
    @Cacheable(value = "nodeNodelistEntry", key = "#zone + '-' + #network + '-' + #node")
    @Transactional(readOnly = true)
    public NodeEntryDto getNodelistEntry(int zone, int network, int node) {
        log.debug("Fetching nodelist entry for zone: {}, network: {}, node: {}", zone, network, node);
        return nodeEntryMapper.toDto(nodelistEntryRepository.getLast(zone, network, node));
    }
}
