package ru.oldzoomer.nodehistj_history_diff.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ru.oldzoomer.nodehistj_history_diff.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;

/**
 * Mapper interface for converting between NodeEntry entity and NodeEntryDto.
 * <p>
 * Provides mapping methods for:
 * - Converting a list of NodeEntry entities to a list of NodeEntryDto objects
 * - Converting a single NodeEntry entity to a NodeEntryDto object
 * <p>
 * Uses MapStruct framework for automatic mapping between entity and DTO.
 * <p>
 * This mapper interface is used to transform NodeEntry entities to their corresponding
 * DTOs for data transfer between different layers of the application.
 * <p>
 * The mapper follows the Spring component model and can be autowired in Spring
 * components for easy integration.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeEntryMapper {
    /**
     * Converts a list of NodeEntry entities to a list of NodeEntryDto objects.
     *
     * @param nodeEntries List of NodeEntry entities to convert
     * @return List of converted NodeEntryDto objects
     */
    List<NodeEntryDto> toDto(List<NodeEntry> nodeEntries);

    /**
     * Converts a single NodeEntry entity to a NodeEntryDto object.
     *
     * @param nodeEntry NodeEntry entity to convert
     * @return Converted NodeEntryDto object
     */
    NodeEntryDto toDto(NodeEntry nodeEntry);
}