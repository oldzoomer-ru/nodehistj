package ru.oldzoomer.nodehistj_history_diff.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

/**
 * Summary of node changes for a specific date.
 * Contains counts of added, removed, and modified nodes for a particular date.
 */
@Data
public class NodeChangeSummaryDto implements Serializable {
    private LocalDate changeDate;
    private Integer nodelistYear;
    private String nodelistName;
    private Long addedCount;
    private Long removedCount;
    private Long modifiedCount;
    private Long totalChanges;
}