package ru.oldzoomer.nodehistj_history_diff.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

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