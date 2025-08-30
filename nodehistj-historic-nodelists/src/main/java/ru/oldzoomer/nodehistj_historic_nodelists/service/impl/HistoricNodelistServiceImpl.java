package ru.oldzoomer.nodehistj_historic_nodelists.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

/**
 * Implementation of service for working with historic Fidonet nodelists.
 * Provides methods for retrieving nodelist entries based on various criteria.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HistoricNodelistServiceImpl implements HistoricNodelistService {
    private final NodeEntryRepository nodeEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    /**
     * Gets all nodelist entries for a specific year and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @return a list of NodeEntryDto objects representing the nodelist entries
     */
    @Override
    public List<NodeEntryDto> getNodelistEntries(int year, int dayOfYear) {
        log.debug("Fetching all historic nodelist entries for year: {}, day: {}", year, dayOfYear);
        return nodeEntryMapper.toDto(nodeEntryRepository.findAll(String.format("nodelist.%03d", dayOfYear), year));
    }

    /**
     * Gets nodelist entries for a specific zone within a specific year and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @return a list of NodeEntryDto objects representing the nodelist entries for the specified zone
     */
    @Override
    public List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone) {
        log.debug("Fetching historic nodelist entries for year: {}, day: {}, zone: {}", year, dayOfYear, zone);
        return nodeEntryMapper.toDto(nodeEntryRepository.find(zone, String.format("nodelist.%03d", dayOfYear), year));
    }

    /**
     * Gets nodelist entries for a specific network within a specific zone, year, and day of year.
     *
     * @param year the year of the nodelist
     * @param dayOfYear the day of year of the nodelist
     * @param zone the zone of the nodelist entries
     * @param network the network of the nodelist entries
     * @return a list of NodeEntryDto objects representing the nodelist entries for the specified network
     */
    @Override
    public List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone, int network) {
        log.debug("Fetching historic nodelist entries for year: {}, day: {}, zone: {}, network: {}",
                year, dayOfYear, zone, network);
        return nodeEntryMapper.toDto(nodeEntryRepository.find(zone, network,
                String.format("nodelist.%03d", dayOfYear), year));
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
    public NodeEntryDto getNodelistEntry(int year, int dayOfYear, int zone, int network, int node) {
        log.debug("Fetching historic nodelist entry for year: {}, day: {}, zone: {}, network: {}, node: {}",
                year, dayOfYear, zone, network, node);
        return nodeEntryMapper.toDto(nodeEntryRepository.find(zone, network, node,
                String.format("nodelist.%03d", dayOfYear), year));
    }
}
