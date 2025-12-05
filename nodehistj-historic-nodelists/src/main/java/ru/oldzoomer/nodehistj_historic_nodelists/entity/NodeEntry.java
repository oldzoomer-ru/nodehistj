package ru.oldzoomer.nodehistj_historic_nodelists.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.oldzoomer.nodelistj.enums.Keywords;

import java.io.Serializable;
import java.util.List;

/**
 * Entity class representing a node entry in the database.
 * Contains information about a specific node in a nodelist.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table("node_entry")
public class NodeEntry implements Serializable {
    @Id
    @Column("id")
    private Long id;

    @Column("nodelist_entry_id")
    private Long nodelistEntryId;

    @Column("zone")
    private Integer zone;

    @Column("network")
    private Integer network;

    @Column("node")
    private Integer node;

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
}