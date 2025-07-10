package ru.oldzoomer.nodehistj_historic_nodelists.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nodelist_entry",
        indexes = @Index(columnList = "nodelistYear DESC, nodelistName DESC"))
public class NodelistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Changed from AUTO to IDENTITY
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nodelist_year")
    private Integer nodelistYear;

    @Column(name = "nodelist_name")
    private String nodelistName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodelistEntry that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "NodelistEntry{" +
                "id=" + id +
                ", nodelistYear=" + nodelistYear +
                ", nodelistName='" + nodelistName + '\'' +
                '}';
    }
}