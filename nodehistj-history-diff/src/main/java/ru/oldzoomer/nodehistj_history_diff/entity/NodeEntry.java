package ru.oldzoomer.nodehistj_history_diff.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

@Getter
@Setter
@Table("node_entry")
public class NodeEntry {
    @PrimaryKey
    private UUID id;

    private Integer zone;
    private Integer network;
    private Integer node;
    private Keywords keywords;
    private String nodeName;
    private String location;
    private String sysOpName;
    private String phone;
    private Integer baudRate;
    private List<String> flags;
    private Integer nodelistYear;
    private String nodelistName;

    public NodeEntry() {
        this.id = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeEntry)) return false;
        NodeEntry nodeEntry = (NodeEntry) o;
        return Objects.equals(zone, nodeEntry.zone) &&
                Objects.equals(network, nodeEntry.network) &&
                Objects.equals(node, nodeEntry.node) &&
                Objects.equals(nodelistYear, nodeEntry.nodelistYear) &&
                Objects.equals(nodelistName, nodeEntry.nodelistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone, network, node, nodelistYear, nodelistName);
    }
}