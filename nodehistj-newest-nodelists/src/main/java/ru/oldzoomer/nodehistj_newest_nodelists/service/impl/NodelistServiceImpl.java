package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.List;

/**
 * Implementation of service for working with Fidonet nodelists (FTS-0005 standard).
 * Provides methods for retrieving nodelist entries based on various criteria.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NodelistServiceImpl implements NodelistService {
    private final NodeEntryRepository nodeEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    /**
     * Gets all nodelist entries.
     *
     * @return a list of NodeEntryDto objects representing the nodelist entries
     */
    @Override
    public List<NodeEntryDto> getNodelistEntries() {
        log.debug("Fetching all nodelist entries");
        return nodeEntryMapper.toDto(nodeEntryRepository.findAll());
    }

    /**
     * Gets nodelist entries for a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @return a list of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    @Override
    public List<NodeEntryDto> getNodelistEntry(int zone) {
        log.debug("Fetching nodelist entries for zone: {}", zone);
        return nodeEntryMapper.toDto(nodeEntryRepository.findByZoneOrderByIdDesc(zone));
    }

    /**
     * Gets nodelist entries for a specific network within a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a list of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    @Override
    public List<NodeEntryDto> getNodelistEntry(int zone, int network) {
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        return nodeEntryMapper.toDto(nodeEntryRepository.findByZoneAndNetworkOrderByIdDesc(zone, network));
    }

    /**
     * Gets a specific nodelist entry for a node within a specific network and zone.
     *
     * @param zone the zone of the nodelist entry
     * @param network the network of the nodelist entry
     * @param node the node address of the nodelist entry
     * @return a NodeEntryDto object representing the specific nodelist entry
     */
    @Override
    public NodeEntryDto getNodelistEntry(int zone, int network, int node) {
        log.debug("Fetching nodelist entry for zone: {}, network: {}, node: {}", zone, network, node);
        return nodeEntryMapper.toDto(
                nodeEntryRepository.findFirstByZoneAndNetworkAndNodeOrderByIdDesc(zone, network, node)
        );
    }
}
