package ru.oldzoomer.nodehistj_historic_nodelists.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodelistEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;

/**
 * Mapper interface for converting NodelistEntry entities to NodelistEntryDto objects and vice versa.
 * Uses Spring component model and ignores unmapped target properties.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodelistEntryMapper {
    /**
     * Converts a NodelistEntry entity to a NodelistEntryDto object.
     *
     * @param nodelistEntry the NodelistEntry entity to convert
     * @return the converted NodelistEntryDto object
     */
    NodelistEntryDto toDto(NodelistEntry nodelistEntry);

    /**
     * Converts a NodelistEntryDto object to a NodelistEntry entity.
     *
     * @param dto the NodelistEntryDto object to convert
     * @return the converted NodelistEntry entity
     */
    NodelistEntry toEntity(NodelistEntryDto dto); // Optional reverse mapping
}