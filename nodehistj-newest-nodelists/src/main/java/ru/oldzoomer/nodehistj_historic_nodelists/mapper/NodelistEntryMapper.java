package ru.oldzoomer.nodehistj_historic_nodelists.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodelistEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodelistEntryMapper {
    NodelistEntryDto toDto(NodelistEntry nodelistEntry);
}