package ru.oldzoomer.nodehistj_history_diff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

/**
 * Mapper interface for converting NodeHistoryEntry entities to NodeHistoryEntryDto objects.
 * Uses Spring component model and ignores unmapped target properties.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeHistoryEntryMapper {
    /**
     * Converts a single NodeHistoryEntry entity to a NodeHistoryEntryDto object.
     *
     * @param nodeHistoryEntry the NodeHistoryEntry entity to convert
     * @return the converted NodeHistoryEntryDto object
     */
    NodeHistoryEntryDto toDto(NodeHistoryEntry nodeHistoryEntry);
}