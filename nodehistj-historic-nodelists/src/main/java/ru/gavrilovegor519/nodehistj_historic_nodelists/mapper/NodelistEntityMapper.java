package ru.gavrilovegor519.nodehistj_historic_nodelists.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.NodelistDto;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntity;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NodelistEntityMapper {
    List<NodelistDto> toDto(List<NodelistEntity> nodeListEntityList);

    NodelistDto toDto(NodelistEntity nodelistEntity);
}