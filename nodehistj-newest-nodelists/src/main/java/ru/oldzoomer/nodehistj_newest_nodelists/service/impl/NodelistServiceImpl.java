package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of service for working with Fidonet nodelists (FTS-0005 standard).
 * Provides methods for retrieving nodelist entries based on various criteria.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NodelistServiceImpl implements NodelistService {
    private final NodelistEntryRepository nodelistEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    /**
     * Gets all nodelist entries.
     *
     * @return a set of NodeEntryDto objects representing the nodelist entries
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntries() {
        log.debug("Fetching all nodelist entries");
        Set<NodeEntry> nodeEntries = nodelistEntryRepository.latest().getNodeEntries();
        return nodeEntryMapper.toDto(nodeEntries);
    }

    /**
     * Gets nodelist entries for a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntry(int zone) {
        log.debug("Fetching nodelist entries for zone: {}", zone);
        Set<NodeEntry> nodeEntries = nodelistEntryRepository.latest()
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .collect(Collectors.toSet());
        return nodeEntryMapper.toDto(nodeEntries);
    }

    /**
     * Gets nodelist entries for a specific network within a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntry(int zone, int network) {
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        Set<NodeEntry> nodeEntries = nodelistEntryRepository.latest()
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .filter(x -> x.getNetwork().equals(network))
                .collect(Collectors.toSet());
        return nodeEntryMapper.toDto(nodeEntries);
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
        NodeEntry nodeEntry = nodelistEntryRepository.latest()
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .filter(x -> x.getNetwork().equals(network))
                .filter(x -> x.getNode().equals(node))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No nodes found"));
        return nodeEntryMapper.toDto(nodeEntry);
    }
}
