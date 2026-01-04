package ru.oldzoomer.nodehistj_newest_nodelists.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;

/**
 * Mapper interface for converting NodeEntry entities to NodeEntryDto objects.
 * Uses Spring component model and ignores unmapped target properties.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeEntryMapper {
    /**
     * Converts a single NodeEntry entity to a NodeEntryDto object.
     *
     * @param nodeEntry the NodeEntry entity to convert
     * @return the converted NodeEntryDto object
     */
    NodeEntryDto toDto(NodeEntry nodeEntry);
}