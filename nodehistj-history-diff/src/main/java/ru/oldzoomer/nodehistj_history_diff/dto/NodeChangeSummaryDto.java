package ru.oldzoomer.nodehistj_history_diff.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Summary of node changes for a specific date.
 * Contains counts of added, removed, and modified nodes for a particular date.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class NodeChangeSummaryDto implements Serializable {
    private LocalDate changeDate;
    private Integer nodelistYear;
    private String nodelistName;
    private Long addedCount;
    private Long removedCount;
    private Long modifiedCount;
    private Long totalChanges;
}