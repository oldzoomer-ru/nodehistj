package ru.oldzoomer.nodehistj_historic_nodelists.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

@Getter
@Setter
@Table("node_entry")
public class NodeEntry implements Serializable {

    @PrimaryKey
    private NodeEntryKey id;

    @Column("keywords")
    private Keywords keywords;

    @Column("node_name")
    private String nodeName;

    @Column("location")
    private String location;

    @Column("sys_op_name")
    private String sysOpName;

    @Column("phone")
    private String phone;

    @Column("baud_rate")
    private Integer baudRate;

    @Column("flags")
    private List<String> flags;

    public NodeEntry() {
        this.id = new NodeEntryKey();
    }

    public NodeEntry(Integer zone, Integer network, Integer node, Integer nodelistYear, String nodelistName) {
        this.id = new NodeEntryKey(nodelistYear, nodelistName, zone, network, node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeEntry)) {
            return false;
        }
        NodeEntry nodeEntry = (NodeEntry) o;
        return Objects.equals(id, nodeEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}