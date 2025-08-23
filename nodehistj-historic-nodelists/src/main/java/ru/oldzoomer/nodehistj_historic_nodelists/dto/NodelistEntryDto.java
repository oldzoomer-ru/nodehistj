package ru.oldzoomer.nodehistj_historic_nodelists.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * DTO for {@link ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry}
 */
@Data
public class NodelistEntryDto implements Serializable {
    private Integer nodelistYear;
    private String nodelistName;
}