package ru.oldzoomer.nodehistj_history_diff.mapper;

import java.util.List;

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
     * Converts a list of NodeHistoryEntry entities to a list of NodeHistoryEntryDto objects.
     *
     * @param nodeHistoryEntries the list of NodeHistoryEntry entities to convert
     * @return the list of converted NodeHistoryEntryDto objects
     */
    List<NodeHistoryEntryDto> toDto(List<NodeHistoryEntry> nodeHistoryEntries);

    /**
     * Converts a single NodeHistoryEntry entity to a NodeHistoryEntryDto object.
     *
     * @param nodeHistoryEntry the NodeHistoryEntry entity to convert
     * @return the converted NodeHistoryEntryDto object
     */
    NodeHistoryEntryDto toDto(NodeHistoryEntry nodeHistoryEntry);
}