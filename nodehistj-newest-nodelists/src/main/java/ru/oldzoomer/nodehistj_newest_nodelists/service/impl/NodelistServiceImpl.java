package ru.oldzoomer.nodehistj_newest_nodelists.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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
    public List<NodeEntryDto> getNodelistEntries() {
        log.debug("Fetching all nodelist entries");
        return nodeEntryMapper.toDto(nodeEntryRepository.findAll());
    }

    @Override
    public List<NodeEntryDto> getNodelistEntry(int zone) {
        log.debug("Fetching nodelist entries for zone: {}", zone);
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone));
    }

    @Override
    public List<NodeEntryDto> getNodelistEntry(int zone, int network) {
        log.debug("Fetching nodelist entries for zone: {} and network: {}", zone, network);
        return nodeEntryMapper.toDto(nodeEntryRepository.getLast(zone, network));
    }

    @Override
    public NodeEntryDto getNodelistEntry(int zone, int network, int node) {
        log.debug("Fetching nodelist entry for zone: {}, network: {}, node: {}", zone, network, node);
        return nodeEntryMapper.toDto(
            nodeEntryRepository.getLast(zone, network, node)
        );
    }
}
