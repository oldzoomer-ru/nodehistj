package ru.gavrilovegor519.nodehistj_historic_nodelists.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString // Added
@Entity
@Table(name = "nodelist_entry",
        indexes = @Index(columnList = "nodelistYear DESC, nodelistName DESC"))
public class NodelistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Changed from AUTO
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nodelist_year")
    private Integer nodelistYear;

    @Column(name = "nodelist_name")
    private String nodelistName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Added null check
        if (!(o instanceof NodelistEntry that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}