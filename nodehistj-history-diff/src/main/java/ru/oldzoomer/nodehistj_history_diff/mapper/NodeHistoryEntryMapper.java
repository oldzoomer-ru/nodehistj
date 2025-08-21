package ru.oldzoomer.nodehistj_history_diff.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeHistoryEntryMapper {
    List<NodeHistoryEntryDto> toDto(List<NodeHistoryEntry> nodeHistoryEntries);

    @Mapping(target = "zone", source = "key.zone")
    @Mapping(target = "network", source = "key.network")
    @Mapping(target = "node", source = "key.node")
    @Mapping(target = "nodelistYear", source = "key.nodelistYear")
    @Mapping(target = "nodelistName", source = "key.nodelistName")
    @Mapping(target = "changeType", source = "key.changeType")
    NodeHistoryEntryDto toDto(NodeHistoryEntry nodeHistoryEntry);
}