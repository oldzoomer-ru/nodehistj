package ru.gavrilovegor519.nodehistj_historic_nodelists.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodelistDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntity;
import ru.gavrilovegor519.nodehistj_historic_nodelists.mapper.NodelistEntityMapper;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodelistEntityRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.service.NodelistService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodelistServiceImpl implements NodelistService {
    private final NodelistEntityRepository nodelistEntityRepository;
    private final NodelistEntityMapper nodelistEntityMapper;

    /**
     * Get all nodelist entries
     *
     * @return List of nodelist entries
     */
    @Override
    @Cacheable(value = "allDataOfNodelist")
    public List<NodelistDto> getNodelistEntries() {
        List<NodelistEntity> nodelistEntity = nodelistEntityRepository.get();
        return nodelistEntityMapper.toDto(nodelistEntity);
    }

    /**
     * Get zone nodelist entry
     *
     * @param zone zone
     * @return Zone nodelist entry
     */
    @Override
    @Cacheable(value = "zoneNodelistEntry", key = "#zone")
    public List<NodelistDto> getNodelistEntry(int zone) {
        List<NodelistEntity> nodelistEntity = nodelistEntityRepository.get(zone);
        return nodelistEntityMapper.toDto(nodelistEntity);
    }

    /**
     * Get network nodelist entry
     *
     * @param zone    zone
     * @param network network
     * @return Network nodelist entry
     */
    @Override
    @Cacheable(value = "networkNodelistEntry", key = "#zone + '-' + #network")
    public List<NodelistDto> getNodelistEntry(int zone, int network) {
        List<NodelistEntity> nodelistEntity = nodelistEntityRepository.get(zone, network);
        return nodelistEntityMapper.toDto(nodelistEntity);
    }

    /**
     * Get node nodelist entry
     *
     * @param zone    zone
     * @param network network
     * @param node    node address
     * @return Node nodelist entry
     */
    @Override
    @Cacheable(value = "nodeNodelistEntry", key = "#zone + '-' + #network + '-' + #node")
    public NodelistDto getNodelistEntry(int zone, int network, int node) {
        NodelistEntity nodelistEntity = nodelistEntityRepository.get(zone, network, node);
        return nodelistEntityMapper.toDto(nodelistEntity);
    }
}
