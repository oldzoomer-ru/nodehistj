package ru.oldzoomer.nodehistj_historic_nodelists.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for {@link ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry}
 */
@Getter
@Setter
@EqualsAndHashCode
public class NodelistEntryDto {
    Integer nodelistYear;
    String nodelistName;
}