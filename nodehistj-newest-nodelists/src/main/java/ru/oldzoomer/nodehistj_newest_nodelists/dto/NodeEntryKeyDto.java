package ru.oldzoomer.nodehistj_newest_nodelists.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NodeEntryKeyDto implements Serializable {
    private Integer nodelistYear;
    private String nodelistName;

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
}
