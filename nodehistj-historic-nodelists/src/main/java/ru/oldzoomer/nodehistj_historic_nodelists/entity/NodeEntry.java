package ru.oldzoomer.nodehistj_historic_nodelists.entity;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

@Getter
@Setter
@Entity
@Table(name = "node_entry",
        indexes = @Index(columnList = "zone ASC, network ASC, node ASC"))
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