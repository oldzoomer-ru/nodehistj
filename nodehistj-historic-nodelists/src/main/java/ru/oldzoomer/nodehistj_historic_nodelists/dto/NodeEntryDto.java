package ru.oldzoomer.nodehistj_historic_nodelists.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * DTO for Fidonet node entry (FTS-0005 standard).
 * Contains all required fields for node listing with validation constraints.
 */
@Data
public class NodeEntryDto implements Serializable {

    private Integer zone;

    private Integer network;

    private Integer node;

    private Keywords keywords;

    private String nodeName;

    private String location;

    private String sysOpName;

    private String phone;

    private Integer baudRate;

    private List<String> flags;
}