package ru.oldzoomer.nodehistj_history_diff.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import ru.oldzoomer.nodelistj.enums.Keywords;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Table("node_history_entry")
public class NodeHistoryEntry implements Serializable {
    @PrimaryKey
    private NodeEntryKey id;

    private ChangeType changeType;

    private Keywords keywords;
    private String nodeName;
    private String location;
    private String sysOpName;
    private String phone;
    private Integer baudRate;
    private List<String> flags;
    private Keywords prevKeywords;
    private String prevNodeName;
    private String prevLocation;
    private String prevSysOpName;
    private String prevPhone;
    private Integer prevBaudRate;
    private List<String> prevFlags;

    public NodeHistoryEntry() {
        this.id = new NodeEntryKey();
    }

    public NodeHistoryEntry(Integer zone, Integer network, Integer node, Integer nodelistYear,
            String nodelistName, ChangeType changeType) {
        this.id = new NodeEntryKey(zone, network, node, nodelistYear, nodelistName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeHistoryEntry that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum ChangeType {
        ADDED, REMOVED, MODIFIED
    }
}