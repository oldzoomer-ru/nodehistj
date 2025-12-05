package ru.oldzoomer.nodehistj_newest_nodelists.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a nodelist entry in the database.
 * Contains information about a specific nodelist.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table("nodelist_entry")
public class NodelistEntry implements Serializable {
    @Id
    @Column("id")
    private Long id;

    @Column("nodelist_year")
    private Integer nodelistYear;

    @Column("nodelist_name")
    private String nodelistName;

    @MappedCollection(idColumn = "nodelist_entry_id")
    @EqualsAndHashCode.Exclude
    private Set<NodeEntry> nodeEntries = new HashSet<>();
}