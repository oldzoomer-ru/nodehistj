package ru.oldzoomer.nodehistj_historic_nodelists.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

/**
 * Implementation of service for working with historic Fidonet nodelists.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HistoricNodelistServiceImpl implements HistoricNodelistService {
    private final NodeEntryRepository nodeEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    @Override
    @Cacheable(value = "historicAllData", key = "{#year, #dayOfYear}")
    public List<NodeEntryDto> getNodelistEntries(int year, int dayOfYear) {
        log.debug("Fetching all historic nodelist entries for year: {}, day: {}", year, dayOfYear);
        return nodeEntryMapper.toDto(nodeEntryRepository.findByYearAndDayOfYear(year, dayOfYear));
    }

    @Override
    @Cacheable(value = "historicZoneData", key = "{#year, #dayOfYear, #zone}")
    public List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone) {
        log.debug("Fetching historic nodelist entries for year: {}, day: {}, zone: {}", year, dayOfYear, zone);
        return nodeEntryMapper.toDto(nodeEntryRepository.findByYearAndDayOfYearAndZone(year, dayOfYear, zone));
    }

    @Override
    @Cacheable(value = "historicNetworkData", key = "{#year, #dayOfYear, #zone, #network}")
    public List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone, int network) {
        log.debug("Fetching historic nodelist entries for year: {}, day: {}, zone: {}, network: {}",
            year, dayOfYear, zone, network);
        List<NodeEntry> entries = nodeEntryRepository
            .findByYearAndDayOfYearAndZoneAndNetwork(year, dayOfYear, zone, network);
        return nodeEntryMapper.toDto(entries);
    }

    @Override
    @Cacheable(value = "historicNodeData", key = "{#year, #dayOfYear, #zone, #network, #node}")
    public NodeEntryDto getNodelistEntry(int year, int dayOfYear, int zone, int network, int node) {
        log.debug("Fetching historic nodelist entry for year: {}, day: {}, zone: {}, network: {}, node: {}", 
            year, dayOfYear, zone, network, node);
        return nodeEntryMapper.toDto(nodeEntryRepository.findByYearAndDayOfYearAndZoneAndNetworkAndNode(
            year, dayOfYear, zone, network, node));
    }
}
