package ru.gavrilovegor519.nodehistj_historic_nodelists.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodelistEntryDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntry;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodelistEntryMapper {
    NodelistEntryDto toDto(NodelistEntry nodelistEntry);
}