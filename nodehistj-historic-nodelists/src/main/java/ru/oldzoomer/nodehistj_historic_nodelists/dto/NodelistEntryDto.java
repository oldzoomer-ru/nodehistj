package ru.oldzoomer.nodehistj_historic_nodelists.dto;

import lombok.Data;

/**
 * DTO for {@link ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry}
 */
@Data
public class NodelistEntryDto {
    private Integer nodelistYear;
    private String nodelistName;
}