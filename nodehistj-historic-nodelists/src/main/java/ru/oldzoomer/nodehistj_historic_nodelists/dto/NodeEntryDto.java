package ru.oldzoomer.nodehistj_historic_nodelists.dto;

import java.util.List;

import lombok.Data;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * DTO for {@link ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry}
 */
@Data
public class NodeEntryDto {
    private Integer nodelistYear; // Denormalized from NodelistEntry
    private String nodelistName; // Denormalized from NodelistEntry
    private Keywords keywords;
    private String nodeName;
    private String location;
    private String sysOpName;
    private String phone;
    private Integer baudRate;
    private List<String> flags;
}