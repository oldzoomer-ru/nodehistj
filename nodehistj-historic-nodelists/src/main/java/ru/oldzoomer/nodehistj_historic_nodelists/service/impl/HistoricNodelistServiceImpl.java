package ru.oldzoomer.nodehistj_historic_nodelists.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

/**
 * Historic nodelist service layer
 */
@Service
@RequiredArgsConstructor
public class HistoricNodelistServiceImpl implements HistoricNodelistService {
    private final NodeEntryRepository nodelistEntryRepository;
    private final NodeEntryMapper nodelistEntryMapper;

    /**
     * Get all nodelist entries
     *
     * @param year      year
     * @param dayOfYear day of year
     * @return List of nodelist entries
     */
    @Override
    @Cacheable(value = "historicAllDataOfNodelist")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntries(int year, int dayOfYear) {
        String nodelistPattern = String.format("nodelist.%03d", dayOfYear);
        return nodelistEntryMapper.toDto(
            nodelistEntryRepository.getAll(nodelistPattern, year)
        );
    }

    /**
     * Get zone nodelist entry
     *
     * @param year      year
     * @param dayOfYear day of year
     * @param zone      zone
     * @return Zone nodelist entry
     */
    @Override
    @Cacheable(value = "historicGetZoneNodelistEntry",
            key = "#year + '-' + #dayOfYear + '-' + #zone")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone) {
        String nodelistPattern = String.format("nodelist.%03d", dayOfYear);
        return nodelistEntryMapper.toDto(
            nodelistEntryRepository.get(zone, nodelistPattern, year)
        );
    }

    /**
     * Get network nodelist entry
     *
     * @param year      year
     * @param dayOfYear day of year
     * @param zone      zone
     * @param network   network
     * @return Network nodelist entry
     */
    @Override
    @Cacheable(value = "historicGetNetworkNodelistEntry",
            key = "#year + '-' + #dayOfYear + '-' + #zone + '-' + #network")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int year, int dayOfYear, int zone, int network) {
        String nodelistPattern = String.format("nodelist.%03d", dayOfYear);
        return nodelistEntryMapper.toDto(
            nodelistEntryRepository.get(zone, network, nodelistPattern, year)
        );
    }

    /**
     * Get node nodelist entry
     *
     * @param year      year
     * @param dayOfYear day of year
     * @param zone      zone
     * @param network   network
     * @param node      node address
     * @return Node nodelist entry
     */
    @Override
    @Cacheable(value = "historicGetNodeNodelistEntry",
            key = "#year + '-' + #dayOfYear + '-' + #zone + '-' + #network + '-' + #node")
    @Transactional(readOnly = true)
    public NodeEntryDto getNodelistEntry(int year, int dayOfYear, int zone, int network, int node) {
        return nodelistEntryMapper.toDto(nodelistEntryRepository.get(
                zone, network, node, String.format("nodelist.%03d", dayOfYear), year));
    }
}
