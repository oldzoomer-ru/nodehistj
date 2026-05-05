package ru.oldzoomer.nodehistj_history_diff.dto;

import ru.oldzoomer.nodelistj.enums.Keywords;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry}.
 * Contains information about a node's history entry, including current and previous values.
 */
public record NodeHistoryEntryDto(
        Integer zone,
        Integer network,
        Integer node,
        LocalDate changeDate,
        Integer nodelistYear,
        Integer dayOfYear,
        ChangeType changeType,
        Keywords keywords,
        String nodeName,
        String location,
        String sysOpName,
        String phone,
        Integer baudRate,
        List<String> flags,
    // Previous values for MODIFIED entries
        Keywords prevKeywords,
        String prevNodeName,
        String prevLocation,
        String prevSysOpName,
        String prevPhone,
        Integer prevBaudRate,
        List<String> prevFlags
) implements Serializable {

    /**
     * Enum representing the type of change made to a node.
     */
    public enum ChangeType {
        ADDED, REMOVED, MODIFIED
    }
}
