package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of service for working with Fidonet nodelists (FTS-0005 standard).
 * Provides methods for retrieving nodelist entries based on various criteria.
 * <p>
 * This service is designed to work with the newest nodelist data, allowing
 * retrieval of node entries by zone, network and node. The service handles
 * complex filtering operations while maintaining performance through optimized
 * database queries.
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
    @Cacheable(value = "nodelistRequests", sync = true)
    public Set<NodeEntryDto> getNodelistEntries() {
        log.debug("Fetching all nodelist entries");
        return getFilteredNodeEntries(null, null, null);
    }

    /**
     * Gets nodelist entries for a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    @Override
    @Cacheable(value = "nodelistRequests", sync = true)
    public Set<NodeEntryDto> getNodelistEntry(Integer zone) {
        log.debug("Fetching nodelist entries for zone: {}", zone);
        return getFilteredNodeEntries(zone, null, null);
    }

    /**
     * Gets nodelist entries for a specific network within a specific zone.
     *
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    @Override
    @Cacheable(value = "nodelistRequests", sync = true)
    public Set<NodeEntryDto> getNodelistEntry(Integer zone, Integer network) {
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        return getFilteredNodeEntries(zone, network, null);
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
    @Cacheable(value = "nodelistRequests", unless = "#result == null")
    public Optional<NodeEntryDto> getNodelistEntry(Integer zone, Integer network, Integer node) {
        log.debug("Fetching nodelist entry for zone: {}, network: {}, node: {}", zone, network, node);
        var result = getFilteredNodeEntries(zone, network, node);
        if (result.isEmpty()) {
            log.warn("No nodelist entry found for zone: {}, network: {}, node: {}", zone, network, node);
            return Optional.empty();
        }
        return result.stream().findFirst();
    }

    /**
     * Helper method to filter node entries based on provided criteria.
     *
     * @param zone the zone to filter by (optional)
     * @param network the network to filter by (optional)
     * @param node the node to filter by (optional)
     * @return a set of NodeEntryDto objects matching the criteria
     */
    private Set<NodeEntryDto> getFilteredNodeEntries(Integer zone, Integer network, Integer node) {
        var nodelistEntry = nodelistEntryRepository.findFirstBy();
        if (nodelistEntry == null) {
            log.warn("No nodelist entry found");
            return Set.of();
        }

        var nodeEntries = nodelistEntry.getNodeEntries().stream();

        if (zone != null) {
            nodeEntries = nodeEntries.filter(x -> x.getZone().equals(zone));
        }
        if (network != null) {
            nodeEntries = nodeEntries.filter(x -> x.getNetwork().equals(network));
        }
        if (node != null) {
            nodeEntries = nodeEntries.filter(x -> x.getNode().equals(node));
        }

        return nodeEntries
                .map(nodeEntryMapper::toDto)
                .collect(Collectors.toUnmodifiableSet());
    }
}
