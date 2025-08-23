package ru.oldzoomer.nodehistj_history_diff.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Summary of node changes for a specific date
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