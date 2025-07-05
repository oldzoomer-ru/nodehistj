package ru.gavrilovegor519.nodehistj_history_diff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.gavrilovegor519.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.gavrilovegor519.nodehistj_history_diff.entity.NodeHistoryEntry;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodeHistoryEntryMapper {
    List<NodeHistoryEntryDto> toDto(List<NodeHistoryEntry> nodeHistoryEntries);
    NodeHistoryEntryDto toDto(NodeHistoryEntry nodeHistoryEntry);
}