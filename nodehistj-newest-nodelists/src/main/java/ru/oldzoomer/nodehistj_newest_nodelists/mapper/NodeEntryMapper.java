package ru.oldzoomer.nodehistj_newest_nodelists.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeEntryMapper {
    List<NodeEntryDto> toDto(List<NodeEntry> nodeEntries);

    NodeEntryDto toDto(NodeEntry nodeEntry);
}