package ru.oldzoomer.nodehistj_historic_nodelists.service.impl;

import java.time.Year;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

/**
 * Implementation of service for working with historic Fidonet nodelists.
 * Provides methods for retrieving nodelist entries based on various criteria.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class HistoricNodelistServiceImpl implements HistoricNodelistService {
    private final NodelistEntryRepository nodelistEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    /**
     * Gets all nodelist entries for a specific year and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @return a set of NodeEntryDto objects representing the nodelist entries
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntries(Year year, Integer dayOfYear) {
        log.debug("Fetching all historic nodelist entries for year: {}, day: {}", year, dayOfYear);
        return nodelistEntryRepository
                .findFirstByNodelistYearAndDayOfYear(year, dayOfYear)
                .getNodeEntries()
                .stream()
                .map(nodeEntryMapper::toDto)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets nodelist entries for a specific zone within a specific year and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntry(Year year, Integer dayOfYear, Integer zone) {
        log.debug("Fetching historic nodelist entries for year: {}, day: {}, zone: {}", year, dayOfYear, zone);
        return nodelistEntryRepository
                .findFirstByNodelistYearAndDayOfYear(year, dayOfYear)
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .map(nodeEntryMapper::toDto)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets nodelist entries for a specific network within a specific zone, year, and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a set of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    @Override
    public Set<NodeEntryDto> getNodelistEntry(Year year, Integer dayOfYear, Integer zone, Integer network) {
        log.debug("Fetching historic nodelist entries for year: {}, day: {}, zone: {}, network: {}",
                year, dayOfYear, zone, network);
        return nodelistEntryRepository
                .findFirstByNodelistYearAndDayOfYear(year, dayOfYear)
                .getNodeEntries()
                .stream()
                .filter(x -> x.getZone().equals(zone))
                .filter(x -> x.getNetwork().equals(network))
                .map(nodeEntryMapper::toDto)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets a specific nodelist entry for a node within a specific network, zone, year, and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entry
     * @param network the network of the nodelist entry
     * @param node the node address of the nodelist entry
     * @return a NodeEntryDto object representing the specific nodelist entry
     */
    @Override
    public NodeEntryDto getNodelistEntry(Year year, Integer dayOfYear, Integer zone, Integer network, Integer node) {
        log.debug("Fetching historic nodelist entry for year: {}, day: {}, zone: {}, network: {}, node: {}",
                year, dayOfYear, zone, network, node);
        return nodelistEntryRepository
                .findFirstByNodelistYearAndDayOfYear(year, dayOfYear)
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
