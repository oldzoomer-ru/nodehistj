package ru.oldzoomer.nodehistj_newest_nodelists.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * DTO for Fidonet node entry (FTS-0005 standard).
 * Contains all required fields for node listing with validation constraints.
 */
@Data
public class NodeEntryDto implements Serializable {
    private NodelistEntryDto nodelistEntry;

    @NotNull
    @Min(1)
    @Max(6)
    private Integer zone;

    @NotNull
    @Min(1)
    @Max(32768)
    private Integer network;

    @NotNull
    @Min(1)
    @Max(32768)
    private Integer node;

    private Keywords keywords;

    @NotNull
    @Size(max = 40)
    private String nodeName;

    @NotNull
    @Size(max = 50)
    private String location;

    @NotNull
    @Size(max = 40)
    private String sysOpName;

    private String phone;

    @Min(300)
    private Integer baudRate;

    @Size(max = 5)
    private List<String> flags;
}