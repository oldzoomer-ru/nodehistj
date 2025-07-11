package ru.oldzoomer.nodehistj_newest_nodelists.dto;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * DTO for {@link ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry}
 */
@Getter
@Setter
@EqualsAndHashCode
public class NodeEntryDto {
    NodelistEntryDto nodelistEntry;
    Keywords keywords;
    String nodeName;
    String location;
    String sysOpName;
    String phone;
    Integer baudRate;
    List<String> flags;
}