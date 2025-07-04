package ru.gavrilovegor519.nodehistj_history_diff.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gavrilovegor519.nodehistj_history_diff.dto.NodeChangeSummaryDto;
import ru.gavrilovegor519.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.gavrilovegor519.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.gavrilovegor519.nodehistj_history_diff.mapper.NodeHistoryEntryMapper;
import ru.gavrilovegor519.nodehistj_history_diff.repo.NodeHistoryEntryRepository;
import ru.gavrilovegor519.nodehistj_history_diff.service.NodeHistoryService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeHistoryServiceImpl implements NodeHistoryService {
    
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;
    private final NodeHistoryEntryMapper nodeHistoryEntryMapper;

    @Override
    @Cacheable(value = "nodeHistory", key = "#zone + '-' + #network + '-' + #node + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getNodeHistory(Integer zone, Integer network, Integer node, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkAndNodeOrderByChangeDateDesc(zone, network, node, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    @Override
    @Cacheable(value = "networkHistory", key = "#zone + '-' + #network + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getNetworkHistory(Integer zone, Integer network, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneAndNetworkOrderByChangeDateDescNodeAsc(zone, network, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    @Override
    @Cacheable(value = "zoneHistory", key = "#zone + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getZoneHistory(Integer zone, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByZoneOrderByChangeDateDescNetworkAscNodeAsc(zone, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    @Override
    @Cacheable(value = "allHistory", key = "#pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getAllHistory(Pageable pageable) {
        return nodeHistoryEntryRepository
                .findAllByOrderByChangeDateDescZoneAscNetworkAscNodeAsc(pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    @Override
    @Cacheable(value = "changesForDate", key = "#date")
    @Transactional(readOnly = true)
    public List<NodeHistoryEntryDto> getChangesForDate(LocalDate date) {
        return nodeHistoryEntryMapper.toDto(
                nodeHistoryEntryRepository.findByChangeDateOrderByZoneAscNetworkAscNodeAsc(date));
    }

    @Override
    @Cacheable(value = "changesBetweenDates", key = "#startDate + '-' + #endDate + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getChangesBetweenDates(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByChangeDateBetweenOrderByChangeDateDescZoneAscNetworkAscNodeAsc(startDate, endDate, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    @Override
    @Cacheable(value = "changesByType", key = "#changeType + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<NodeHistoryEntryDto> getChangesByType(NodeHistoryEntry.ChangeType changeType, Pageable pageable) {
        return nodeHistoryEntryRepository
                .findByChangeTypeOrderByChangeDateDescZoneAscNetworkAscNodeAsc(changeType, pageable)
                .map(nodeHistoryEntryMapper::toDto);
    }

    @Override
    @Cacheable(value = "changeSummary", key = "#startDate + '-' + #endDate")
    @Transactional(readOnly = true)
    public List<NodeChangeSummaryDto> getChangeSummary(LocalDate startDate, LocalDate endDate) {
        return nodeHistoryEntryRepository.getChangeSummary(startDate, endDate);
    }

    @Override
    @Cacheable(value = "mostActiveNodes", key = "#startDate + '-' + #endDate + '-' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public List<Object[]> getMostActiveNodes(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return nodeHistoryEntryRepository.getMostActiveNodes(startDate, endDate, pageable);
    }
}