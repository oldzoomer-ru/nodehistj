package ru.oldzoomer.nodehistj_historic_nodelists.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryKeyDto;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntryKey;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeEntryMapper {
    List<NodeEntryDto> toDto(List<NodeEntry> nodeEntries);

    NodeEntryDto toDto(NodeEntry nodeEntry);

    NodeEntryKey toKey(NodeEntryDto nodeEntryDto);

    NodeEntryKeyDto toKeyDto(NodeEntryKey nodeEntryKey);
}