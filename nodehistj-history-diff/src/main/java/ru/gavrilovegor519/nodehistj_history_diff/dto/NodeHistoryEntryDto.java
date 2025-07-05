package ru.gavrilovegor519.nodehistj_history_diff.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.gavrilovegor519.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.gavrilovegor519.nodelistj.enums.Keywords;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link NodeHistoryEntry}
 */
@Getter
@Setter
@EqualsAndHashCode
public class NodeHistoryEntryDto {
    Integer zone;
    Integer network;
    Integer node;
    LocalDate changeDate;
    Integer nodelistYear;
    String nodelistName;
    NodeHistoryEntry.ChangeType changeType;
    Keywords keywords;
    String nodeName;
    String location;
    String sysOpName;
    String phone;
    Integer baudRate;
    List<String> flags;
    
    // Previous values for MODIFIED entries
    Keywords prevKeywords;
    String prevNodeName;
    String prevLocation;
    String prevSysOpName;
    String prevPhone;
    Integer prevBaudRate;
    List<String> prevFlags;
}