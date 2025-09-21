package ru.oldzoomer.nodehistj_history_diff.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import ru.oldzoomer.nodelistj.enums.Keywords;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

    public NodeEntry(Integer nodelistYear, String nodelistName, Integer zone, Integer network, Integer node) {
        this.id = new NodeEntryKey(zone, network, node, nodelistYear, nodelistName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeEntry nodeEntry)) {
            return false;
        }
        return Objects.equals(id, nodeEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}