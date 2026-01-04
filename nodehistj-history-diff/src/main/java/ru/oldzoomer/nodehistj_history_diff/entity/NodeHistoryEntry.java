package ru.oldzoomer.nodehistj_history_diff.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.oldzoomer.nodelistj.enums.Keywords;

/**
 * Entity class representing a node history entry in the database.
 * Contains information about changes to a specific node over time.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table("node_history_entry")
public class NodeHistoryEntry implements Serializable {
    @Id
    @Column("id")
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column("zone")
    private Integer zone;

    @Column("network")
    private Integer network;

    @Column("node")
    private Integer node;

    @Column("change_date")
    private LocalDate changeDate;

    @Column("nodelist_year")
    private Year nodelistYear;

    @Column("day_of_year")
    private Integer dayOfYear;

    @Column("change_type")
    private ChangeType changeType;

    @Column("keywords")
    private Keywords keywords;

    @Column("node_name")
    private String nodeName;

    @Column("location")
    private String location;

    @Column("sys_op_name")
    private String sysOpName;

    @Column("phone")
    private String phone;

    @Column("baud_rate")
    private Integer baudRate;

    @Column("flags")
    private List<String> flags;

    // Previous values for MODIFIED entries
    @Column("prev_keywords")
    private Keywords prevKeywords;

    @Column("prev_node_name")
    private String prevNodeName;

    @Column("prev_location")
    private String prevLocation;

    @Column("prev_sys_op_name")
    private String prevSysOpName;

    @Column("prev_phone")
    private String prevPhone;

    @Column("prev_baud_rate")
    private Integer prevBaudRate;

    @Column("prev_flags")
    private List<String> prevFlags;

    /**
     * Enum representing the type of change made to a node.
     */
    public enum ChangeType {
        ADDED, REMOVED, MODIFIED
    }
}