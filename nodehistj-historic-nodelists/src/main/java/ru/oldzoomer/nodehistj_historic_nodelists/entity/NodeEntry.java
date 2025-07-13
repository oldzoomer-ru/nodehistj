package ru.oldzoomer.nodehistj_historic_nodelists.entity;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

@Entity
@Table(name = "node_entry")
@Getter
@Setter
public class NodeEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nodelist_entry_id")
    private NodelistEntry nodelistEntry;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer dayOfYear;

    @Column(nullable = false)
    @Min(1)
    @Max(6)
    private Integer zone;

    @Column(nullable = false)
    @Min(1)
    @Max(32768)
    private Integer network;

    @Column(nullable = false)
    @Min(1)
    @Max(32768)
    private Integer node;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodeEntry nodeEntry)) {
            return false;
        }
        return Objects.equals(id, nodeEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}