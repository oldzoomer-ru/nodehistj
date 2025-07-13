package ru.oldzoomer.nodehistj_newest_nodelists.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a Fidonet nodelist file (FTS-0005 standard).
 */
@Entity
@Table(name = "nodelist_entry")
@Getter
@Setter
public class NodelistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fileName;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @Min(1900)
    @Max(2100)
    private Integer nodelistYear;

    @Column(nullable = false)
    private String nodelistName;

    @OneToMany(mappedBy = "nodelistEntry", cascade = CascadeType.ALL)
    private List<NodeEntry> nodeEntries;
}