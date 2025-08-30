package ru.oldzoomer.nodehistj_newest_nodelists.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * DTO for {@link ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry}.
 * Contains information about a specific nodelist.
 */
@Data
public class NodelistEntryDto implements Serializable {
    Integer nodelistYear;
    String nodelistName;
}