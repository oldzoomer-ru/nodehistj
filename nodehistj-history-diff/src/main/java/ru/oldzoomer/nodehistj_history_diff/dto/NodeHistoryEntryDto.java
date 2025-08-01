package ru.oldzoomer.nodehistj_history_diff.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * DTO for {@link NodeHistoryEntry}
 */
@Getter
@Setter
@EqualsAndHashCode
public class NodeHistoryEntryDto {
    private Integer zone;
    private Integer network;
    private Integer node;
    private LocalDate changeDate;
    private Integer nodelistYear;
    private String nodelistName;
    private NodeHistoryEntry.ChangeType changeType;
    private Keywords keywords;
    private String nodeName;
    private String location;
    private String sysOpName;
    private String phone;
    private Integer baudRate;
    private List<String> flags;
    
    // Previous values for MODIFIED entries
    private Keywords prevKeywords;
    private String prevNodeName;
    private String prevLocation;
    private String prevSysOpName;
    private String prevPhone;
    private Integer prevBaudRate;
    private List<String> prevFlags;
}