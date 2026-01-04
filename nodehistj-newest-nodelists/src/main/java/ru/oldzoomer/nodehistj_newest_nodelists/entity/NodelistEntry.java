package ru.oldzoomer.nodehistj_newest_nodelists.entity;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity class representing a nodelist entry in the database.
 * Contains information about a specific nodelist.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"nodelistYear", "dayOfYear"})
@Table("nodelist_entry")
public class NodelistEntry {
    @Id
    @Column("id")
    private Long id;

    @Column("nodelist_year")
    private Year nodelistYear;

    @Column("day_of_year")
    private Integer dayOfYear;

    @MappedCollection(idColumn = "nodelist_entry_id")
    @ToString.Exclude
    private Set<NodeEntry> nodeEntries = new HashSet<>();
}