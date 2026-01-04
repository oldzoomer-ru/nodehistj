package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

/**
 * Implementation of service for working with Fidonet nodelists (FTS-0005 standard).
 * Provides methods for retrieving nodelist entries based on various criteria.
 */
@Service
@RequiredArgsConstructor
@Log4j2
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
        return nodelistEntryRepository
                .findFirstBy()
                .getNodeEntries()
                .stream()
                .map(nodeEntryMapper::toDto)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets nodelist entries for a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntry(Integer zone) {
        log.debug("Fetching nodelist entries for zone: {}", zone);
        return nodelistEntryRepository
                .findFirstBy()
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .map(nodeEntryMapper::toDto)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets nodelist entries for a specific network within a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntry(Integer zone, Integer network) {
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        return nodelistEntryRepository
                .findFirstBy()
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .filter(x -> x.getNetwork().equals(network))
                .map(nodeEntryMapper::toDto)
                .collect(Collectors.toUnmodifiableSet());
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
    public NodeEntryDto getNodelistEntry(Integer zone, Integer network, Integer node) {
        log.debug("Fetching nodelist entry for zone: {}, network: {}, node: {}", zone, network, node);
        return nodelistEntryRepository
                .findFirstBy()
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .filter(x -> x.getNetwork().equals(network))
                .filter(x -> x.getNode().equals(node))
                .map(nodeEntryMapper::toDto)
                .findFirst()
                .orElseThrow();
    }
}
