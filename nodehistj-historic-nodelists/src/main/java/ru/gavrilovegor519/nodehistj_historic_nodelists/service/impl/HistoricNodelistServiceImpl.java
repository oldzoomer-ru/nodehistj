package ru.gavrilovegor519.nodehistj_historic_nodelists.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodelistDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntity;
import ru.gavrilovegor519.nodehistj_historic_nodelists.mapper.NodelistEntityMapper;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodelistEntityRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.HistoricNodelistService;

import java.util.List;

/**
 * Historic nodelist service layer
 */
@Service
@RequiredArgsConstructor
public class HistoricNodelistServiceImpl implements HistoricNodelistService {
    private final NodelistEntityRepository nodelistEntityRepository;
    private final NodelistEntityMapper nodelistEntityMapper;

    /**
     * Get all nodelist entries
     *
     * @param year      year
     * @param dayOfYear day of year
     * @return List of nodelist entries
     */
    @Override
    @Cacheable(value = "historicAllDataOfNodelist")
    public List<NodelistDto> getNodelistEntries(int year, int dayOfYear) {
        List<NodelistEntity> nodelistEntity = nodelistEntityRepository.get(year, String.format("%03d", year));
        return nodelistEntityMapper.toDto(nodelistEntity);
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
    public List<NodelistDto> getNodelistEntry(int year, int dayOfYear, int zone) {
        List<NodelistEntity> nodelistEntity = nodelistEntityRepository.get(year, String.format("%03d", year), zone);
        return nodelistEntityMapper.toDto(nodelistEntity);
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
    public List<NodelistDto> getNodelistEntry(int year, int dayOfYear, int zone, int network) {
        List<NodelistEntity> nodelistEntity = nodelistEntityRepository.get(year, String.format("%03d", year), zone, network);
        return nodelistEntityMapper.toDto(nodelistEntity);
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
    public NodelistDto getNodelistEntry(int year, int dayOfYear, int zone, int network, int node) {
        NodelistEntity nodelistEntity = nodelistEntityRepository.get(year, String.format("%03d", year), zone, network, node);
        return nodelistEntityMapper.toDto(nodelistEntity);
    }
}
