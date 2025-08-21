package ru.oldzoomer.nodehistj_historic_nodelists.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

@Getter
@Setter
@Table("node_entry")
public class NodeEntry implements Serializable {
    
    @PrimaryKeyClass
    @Getter
    @Setter
    public static class NodeEntryKey implements Serializable {
        @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private UUID id;
        
        @PrimaryKeyColumn(name = "zone", ordinal = 1)
        private Integer zone;
        
        @PrimaryKeyColumn(name = "network", ordinal = 2)
        private Integer network;
        
        @PrimaryKeyColumn(name = "node", ordinal = 3)
        private Integer node;
        
        @PrimaryKeyColumn(name = "nodelist_year", ordinal = 4)
        private Integer nodelistYear;
        
        @PrimaryKeyColumn(name = "nodelist_name", ordinal = 5)
        private String nodelistName;
    }
    
    @PrimaryKey
    private NodeEntryKey key;
    
    private Keywords keywords;
    private String nodeName;
    private String location;
    private String sysOpName;
    private String phone;
    private Integer baudRate;
    private List<String> flags;

    public NodeEntry() {
        this.key = new NodeEntryKey();
        this.key.id = UUID.randomUUID();
    }

    public NodeEntry(Integer zone, Integer network, Integer node, Integer nodelistYear, String nodelistName) {
        this();
        this.key.zone = zone;
        this.key.network = network;
        this.key.node = node;
        this.key.nodelistYear = nodelistYear;
        this.key.nodelistName = nodelistName;
    }

    // Custom setters for key fields to maintain compatibility with existing code
    public void setZone(Integer zone) {
        if (key == null) {
            key = new NodeEntryKey();
        }
        key.setZone(zone);
    }

    public void setNetwork(Integer network) {
        if (key == null) {
            key = new NodeEntryKey();
        }
        key.setNetwork(network);
    }

    public void setNode(Integer node) {
        if (key == null) {
            key = new NodeEntryKey();
        }
        key.setNode(node);
    }

    public void setNodelistYear(Integer nodelistYear) {
        if (key == null) {
            key = new NodeEntryKey();
        }
        key.setNodelistYear(nodelistYear);
    }

    public void setNodelistName(String nodelistName) {
        if (key == null) {
            key = new NodeEntryKey();
        }
        key.setNodelistName(nodelistName);
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
        return Objects.equals(key, nodeEntry.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}