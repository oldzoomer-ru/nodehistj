package ru.gavrilovegor519.nodehistj_historic_nodelists.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.gavrilovegor519.nodelistj.enums.Keywords;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "node_entry")
public class NodeEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private NodelistEntry nodelistEntry;

    @Column(name = "zone")
    private Integer zone;

    @Column(name = "network")
    private Integer network;

    @Column(name = "node")
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
        if (!(o instanceof NodeEntry nodeEntry)) return false;
        return Objects.equals(id, nodeEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}