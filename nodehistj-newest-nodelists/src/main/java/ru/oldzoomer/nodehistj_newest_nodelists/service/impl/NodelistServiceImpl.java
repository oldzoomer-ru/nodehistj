package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.mapper.NodeEntryMapper;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

/**
 * Implementation of service for working with Fidonet nodelists (FTS-0005 standard).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NodelistServiceImpl implements NodelistService {
    private final NodeEntryRepository nodeEntryRepository;
    private final NodeEntryMapper nodeEntryMapper;

    @Override
    @Cacheable(value = "allDataOfNodelist")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntries() {
        log.debug("Fetching all nodelist entries");
        return nodeEntryMapper.toDto(nodeEntryRepository.getAll());
    }

    @Override
    @Cacheable(value = "zoneNodelistEntry", key = "#zone")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int zone) {
        log.debug("Fetching nodelist entries for zone: {}", zone);
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone));
    }

    @Override
    @Cacheable(value = "networkNodelistEntry", key = "#zone + '-' + #network")
    @Transactional(readOnly = true)
    public List<NodeEntryDto> getNodelistEntry(int zone, int network) {
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone, network));
    }

    @Override
    @Cacheable(value = "nodeNodelistEntry", key = "#zone + '-' + #network + '-' + #node")
    @Transactional(readOnly = true)
    public NodeEntryDto getNodelistEntry(int zone, int network, int node) {
        log.debug("Fetching nodelist entry for zone: {}, network: {}, node: {}", zone, network, node);
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone, network, node));
    }
}
