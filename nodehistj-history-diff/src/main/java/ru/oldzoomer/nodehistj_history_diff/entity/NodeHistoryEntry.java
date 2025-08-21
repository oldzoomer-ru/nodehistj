package ru.oldzoomer.nodehistj_history_diff.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

@Getter
@Setter
@Table("node_history_entry")
public class NodeHistoryEntry {
    
    @PrimaryKeyClass
    @Getter
    @Setter
    public static class NodeHistoryEntryKey implements Serializable {
        @PrimaryKeyColumn(name = "zone", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private Integer zone;
        
        @PrimaryKeyColumn(name = "network", ordinal = 1)
        private Integer network;
        
        @PrimaryKeyColumn(name = "node", ordinal = 2)
        private Integer node;
        
        @PrimaryKeyColumn(name = "nodelist_year", ordinal = 3)
        private Integer nodelistYear;
        
        @PrimaryKeyColumn(name = "nodelist_name", ordinal = 4)
        private String nodelistName;
        
        @PrimaryKeyColumn(name = "change_type", ordinal = 5)
        private ChangeType changeType;
        
        @PrimaryKeyColumn(name = "id", ordinal = 6)
        private UUID id;
    }
    
    @PrimaryKey
    private NodeHistoryEntryKey key;
    
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
    
    private LocalDate changeDate;

    public NodeHistoryEntry() {
        this.key = new NodeHistoryEntryKey();
        this.key.id = UUID.randomUUID();
    }

    public NodeHistoryEntry(Integer zone, Integer network, Integer node, Integer nodelistYear,
                           String nodelistName, ChangeType changeType) {
        this();
        this.key.zone = zone;
        this.key.network = network;
        this.key.node = node;
        this.key.nodelistYear = nodelistYear;
        this.key.nodelistName = nodelistName;
        this.key.changeType = changeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeHistoryEntry)) {
            return false;
        }
        NodeHistoryEntry that = (NodeHistoryEntry) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public enum ChangeType {
        ADDED, REMOVED, MODIFIED
    }

    // Публичные методы доступа к полям ключа для обратной совместимости
    public Integer getZone() {
        return key != null ? key.getZone() : null;
    }

    public Integer getNetwork() {
        return key != null ? key.getNetwork() : null;
    }

    public Integer getNode() {
        return key != null ? key.getNode() : null;
    }

    public Integer getNodelistYear() {
        return key != null ? key.getNodelistYear() : null;
    }

    public String getNodelistName() {
        return key != null ? key.getNodelistName() : null;
    }

    public ChangeType getChangeType() {
        return key != null ? key.getChangeType() : null;
    }

    // Публичные методы установки полей ключа для обратной совместимости
    public void setZone(Integer zone) {
        if (key != null) {
            key.setZone(zone);
        }
    }

    public void setNetwork(Integer network) {
        if (key != null) {
            key.setNetwork(network);
        }
    }

    public void setNode(Integer node) {
        if (key != null) {
            key.setNode(node);
        }
    }

    public void setNodelistYear(Integer nodelistYear) {
        if (key != null) {
            key.setNodelistYear(nodelistYear);
        }
    }

    public void setNodelistName(String nodelistName) {
        if (key != null) {
            key.setNodelistName(nodelistName);
        }
    }

    public void setChangeType(ChangeType changeType) {
        if (key != null) {
            key.setChangeType(changeType);
        }
    }

    public void setId(UUID id) {
        if (key != null) {
            key.setId(id);
        }
    }

    public LocalDate getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }
}