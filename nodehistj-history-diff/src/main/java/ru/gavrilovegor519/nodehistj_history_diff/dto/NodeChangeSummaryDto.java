package ru.gavrilovegor519.nodehistj_history_diff.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Summary of node changes for a specific date
 */
@Getter
@Setter
@EqualsAndHashCode
public class NodeChangeSummaryDto {
    LocalDate changeDate;
    Integer nodelistYear;
    String nodelistName;
    Long addedCount;
    Long removedCount;
    Long modifiedCount;
    Long totalChanges;
}