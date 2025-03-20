package ru.gavrilovegor519.nodehistj_historic_nodelists.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "nodelist_entry")
public class NodelistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nodelist_year")
    private Integer nodelistYear;

    @Column(name = "nodelist_name")
    private String nodelistName;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodelistEntry that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}