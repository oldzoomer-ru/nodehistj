package ru.oldzoomer.nodehistj_historic_nodelists.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * Entity class representing a node entry in the database.
 * Contains information about a specific node in a nodelist.
 */
@Getter
@Setter
@Entity
@Table(name = "node_entry",
        indexes = @Index(columnList = "zone ASC, network ASC, node ASC"))
@NamedEntityGraph(name = "NodeEntry.nodelistEntry",
        attributeNodes = @NamedAttributeNode("nodelistEntry"))
public class NodeEntry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private NodelistEntry nodelistEntry;

    @Column(name = "zone")
    private Integer zone;

    @Column(name = "network")
    private Integer network;

    @Column(name = "node")
    private Integer node;

    @Column(name = "keywords")
    private Keywords keywords;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "location")
    private String location;

    @Column(name = "sys_op_name")
    private String sysOpName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "baud_rate")
    private Integer baudRate;

    @Column(name = "flags")
    private List<String> flags;

    /**
     * Compares this NodeEntry with another object for equality.
     * Two NodeEntry objects are considered equal if they have the same ID.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodeEntry nodeEntry)) {
            return false;
        }
        return Objects.equals(id, nodeEntry.id);
    }

    /**
     * Returns a hash code value for this NodeEntry.
     * The hash code is based on the ID of the NodeEntry.
     *
     * @return a hash code value for this NodeEntry
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}