package ru.gavrilovegor519.nodehistj_historic_nodelists.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodeEntry;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {NodelistEntryMapper.class})
public interface NodeEntryMapper {
    List<NodeEntryDto> toDto(List<NodeEntry> nodeEntries);

    NodeEntry toEntity(NodeEntryDto nodeEntryDto);

    NodeEntryDto toDto(NodeEntry nodeEntry);
}