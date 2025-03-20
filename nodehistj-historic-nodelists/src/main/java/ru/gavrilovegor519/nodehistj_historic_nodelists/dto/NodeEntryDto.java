package ru.gavrilovegor519.nodehistj_historic_nodelists.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.gavrilovegor519.nodelistj.enums.Keywords;

import java.util.List;

/**
 * DTO for {@link ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodeEntry}
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