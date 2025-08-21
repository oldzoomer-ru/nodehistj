package ru.oldzoomer.nodehistj_history_diff.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeEntryMapper {
    List<NodeEntryDto> toDto(List<NodeEntry> nodeEntries);

    @Mapping(target = "zone", expression = "java(nodeEntry.getKey().getZone())")
    @Mapping(target = "network", expression = "java(nodeEntry.getKey().getNetwork())")
    @Mapping(target = "node", expression = "java(nodeEntry.getKey().getNode())")
    @Mapping(target = "nodelistYear", expression = "java(nodeEntry.getKey().getNodelistYear())")
    @Mapping(target = "nodelistName", expression = "java(nodeEntry.getKey().getNodelistName())")
    NodeEntryDto toDto(NodeEntry nodeEntry);
}