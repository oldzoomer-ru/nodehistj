package ru.oldzoomer.nodehistj_history_diff.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for change summary statistics
 */
@Data
@Builder
public class ChangeSummaryDto implements Serializable {
    private int addedCount;
    private int modifiedCount;
    private int removedCount;
    private String date;
}