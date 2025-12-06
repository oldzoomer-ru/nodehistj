package ru.oldzoomer.nodehistj_history_diff.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.oldzoomer.nodelistj.enums.Keywords;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity class representing a node history entry in the database.
 * Contains information about changes to a specific node over time.
 */
@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "node_history_entry",
        indexes = {
            @Index(columnList = "zone ASC, network ASC, node ASC, changeDate DESC"),
            @Index(columnList = "changeDate DESC"),
            @Index(columnList = "changeType")
        })
public class NodeHistoryEntry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "zone", nullable = false)
    private Integer zone;

    @Column(name = "network", nullable = false)
    private Integer network;

    @Column(name = "node", nullable = false)
    private Integer node;

    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;

    @Column(name = "nodelist_year", nullable = false)
    private Integer nodelistYear;

    @Column(name = "nodelist_name", nullable = false)
    private String nodelistName;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

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

    // Previous values for MODIFIED entries
    @Column(name = "prev_keywords")
    private Keywords prevKeywords;

    @Column(name = "prev_node_name")
    private String prevNodeName;

    @Column(name = "prev_location")
    private String prevLocation;

    @Column(name = "prev_sys_op_name")
    private String prevSysOpName;

    @Column(name = "prev_phone")
    private String prevPhone;

    @Column(name = "prev_baud_rate")
    private Integer prevBaudRate;

    @Column(name = "prev_flags")
    private List<String> prevFlags;

    /**
     * Enum representing the type of change made to a node.
     */
    public enum ChangeType {
        ADDED, REMOVED, MODIFIED
    }
}