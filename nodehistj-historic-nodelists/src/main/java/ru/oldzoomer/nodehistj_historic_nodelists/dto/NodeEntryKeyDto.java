package ru.oldzoomer.nodehistj_historic_nodelists.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for NodeEntryKey entity.
 * <p>
 * Represents the composite primary key for a node entry in the system, containing:
 * - Nodelist year
 * - Nodelist name
 * - Zone identifier (1-6)
 * - Network identifier (1-32768)
 * - Node identifier (1-32768)
 * <p>
 * This DTO is used for transferring node entry key information between layers.
 * <p>
 * Validation constraints ensure that:
 * - Zone must be between 1 and 6
 * - Network must be between 1 and 32768
 * - Node must be between 1 and 32768
 * <p>
 * The constraints reflect the Fidonet addressing standards (FTS-0005).
 * <p>
 * This DTO provides a data transfer object for the composite key of a node entry,
 * ensuring that the key components are properly validated according to Fidonet
 * standards before being used in the system.
 */
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
