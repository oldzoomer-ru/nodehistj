package ru.oldzoomer.nodehistj_newest_nodelists.dto;

import ru.oldzoomer.nodelistj.enums.Keywords;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for Fidonet node entry (FTS-0005 standard).
 * Contains all required fields for node listing with validation constraints.
 */
public record NodeEntryDto(
        Integer zone,
        Integer network,
        Integer node,
        Keywords keywords,
        String nodeName,
        String location,
        String sysOpName,
        String phone,
        Integer baudRate,
        List<String> flags
) implements Serializable {
}
