package ru.oldzoomer.nodehistj_history_diff.entity;

import java.io.Serializable;
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
import lombok.ToString;

/**
 * Entity class representing a nodelist entry in the database.
 * Contains information about a specific nodelist.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "nodelist_entry",
        indexes = @Index(columnList = "nodelistYear DESC, nodelistName DESC"))
public class NodelistEntry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nodelist_year")
    private Integer nodelistYear;

    @Column(name = "nodelist_name")
    private String nodelistName;

    /**
     * Compares this NodelistEntry with another object for equality.
     * Two NodelistEntry objects are considered equal if they have the same ID.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
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

    /**
     * Returns a hash code value for this NodelistEntry.
     * The hash code is based on the ID of the NodelistEntry.
     *
     * @return a hash code value for this NodelistEntry
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}