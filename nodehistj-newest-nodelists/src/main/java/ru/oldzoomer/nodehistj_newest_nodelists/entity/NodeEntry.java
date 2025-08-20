package ru.oldzoomer.nodehistj_newest_nodelists.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;
import ru.oldzoomer.nodelistj.enums.Keywords;

@Table("node_entry")
@Getter
@Setter
public class NodeEntry {
    @PrimaryKey
    private UUID id;

    @Indexed
    private Integer nodelistYear;

    @Indexed
    private String nodelistName;
    
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

    public NodeEntry() {
        this.id = UUID.randomUUID();
    }

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