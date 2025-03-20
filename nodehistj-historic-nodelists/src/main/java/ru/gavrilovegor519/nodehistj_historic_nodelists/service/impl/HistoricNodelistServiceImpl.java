package ru.gavrilovegor519.nodehistj_historic_nodelists.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.mapper.NodeEntryMapper;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.HistoricNodelistService;

import java.util.List;

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
        return nodelistEntryMapper.toDto(nodelistEntryRepository.getAll(String.format("%03d", year), year));
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
        return nodelistEntryMapper.toDto(nodelistEntryRepository.get(year, String.format("%03d", year), zone));
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
        return nodelistEntryMapper.toDto(nodelistEntryRepository.get(zone, network, String.format("%03d", year), zone));
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
                zone, network, node, String.format("%03d", year), zone));
    }
}
