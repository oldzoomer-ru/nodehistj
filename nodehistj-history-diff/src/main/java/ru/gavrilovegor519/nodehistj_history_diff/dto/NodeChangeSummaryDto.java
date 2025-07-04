package ru.gavrilovegor519.nodehistj_history_diff.dto;

import java.time.LocalDate;

import lombok.*;

/**
 * Summary of node changes for a specific date
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class NodeChangeSummaryDto {
    LocalDate changeDate;
    Integer nodelistYear;
    String nodelistName;
    Long addedCount;
    Long removedCount;
    Long modifiedCount;
    Long totalChanges;
}