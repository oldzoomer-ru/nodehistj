package ru.oldzoomer.nodehistj_historic_nodelists.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;

import java.util.Set;

/**
 * Mapper interface for converting NodeEntry entities to NodeEntryDto objects.
 * Uses Spring component model and ignores unmapped target properties.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeEntryMapper {
    /**
     * Converts a list of NodeEntry entities to a list of NodeEntryDto objects.
     *
     * @param nodeEntries the list of NodeEntry entities to convert
     * @return the set of converted NodeEntryDto objects
     */
    Set<NodeEntryDto> toDto(Set<NodeEntry> nodeEntries);

    /**
     * Converts a single NodeEntry entity to a NodeEntryDto object.
     *
     * @param nodeEntry the NodeEntry entity to convert
     * @return the converted NodeEntryDto object
     */
    NodeEntryDto toDto(NodeEntry nodeEntry);
}