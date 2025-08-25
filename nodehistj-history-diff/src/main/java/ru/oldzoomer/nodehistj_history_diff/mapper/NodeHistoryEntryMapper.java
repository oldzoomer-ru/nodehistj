package ru.oldzoomer.nodehistj_history_diff.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeHistoryEntryMapper {
    List<NodeHistoryEntryDto> toDto(List<NodeHistoryEntry> nodeHistoryEntries);

    NodeHistoryEntryDto toDto(NodeHistoryEntry nodeHistoryEntry);
}