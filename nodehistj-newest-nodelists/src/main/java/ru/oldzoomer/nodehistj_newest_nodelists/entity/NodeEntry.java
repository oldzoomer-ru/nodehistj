package ru.oldzoomer.nodehistj_newest_nodelists.entity;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * Entity representing a Fidonet node entry according to FTS-0005 standard.
 * Contains all required fields for a node listing.
 */
@Getter
@Setter
@Entity
@Table(name = "node_entry",
        indexes = @Index(columnList = "zone ASC, network ASC, node ASC"))
public class NodeEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private NodelistEntry nodelistEntry;

    /**
     * Fidonet zone number (1-6)
     */
    @Column(name = "zone")
    @Min(1)
    @Max(6)
    private Integer zone;

    /**
     * Fidonet network number within zone
     */
    @Column(name = "network")
    @Min(1)
    private Integer network;

    /**
     * Fidonet node number within network
     */
    @Column(name = "node")
    @Min(1)
    private Integer node;

    /**
     * Standard Fidonet keywords (e.g. HUB, HOST, REGIONAL etc.)
     */
    @Column(name = "keywords")
    private Keywords keywords;

    /**
     * Node name (up to 40 chars per FTS-0005)
     */
    @Column(name = "node_name", length = 40)
    @Size(max = 40, message = "Node name must be 40 chars or less")
    private String nodeName;

    /**
     * Geographic location (up to 50 chars per FTS-0005)
     */
    @Column(name = "location", length = 50)
    @Size(max = 50, message = "Location must be 50 chars or less")
    private String location;

    /**
     * System operator name (up to 40 chars per FTS-0005)
     */
    @Column(name = "sys_op_name", length = 40)
    @Size(max = 40, message = "Sysop name must be 40 chars or less")
    private String sysOpName;

    /**
     * Phone number in Fidonet standard format (e.g. 123-4567)
     * Must match FTS-0005 phone number requirements
     */
    @Column(name = "phone")
    private String phone;

    @Column(name = "baud_rate")
    private Integer baudRate;

    @Column(name = "flags")
    private List<String> flags;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodeEntry nodeEntry)) {
            return false;
        }
        return Objects.equals(id, nodeEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}