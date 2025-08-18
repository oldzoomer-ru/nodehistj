package ru.oldzoomer.nodehistj_history_diff.entity;

import org.springframework.data.cassandra.core.mapping.*;
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
    @PrimaryKey
    private UUID id;

    @Indexed
    private Integer zone;

    @Indexed
    private Integer network;

    @Indexed
    private Integer node;

    @Indexed
    private LocalDate changeDate;

    @Indexed
    private Integer nodelistYear;

    @Indexed
    private String nodelistName;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeHistoryEntry that = (NodeHistoryEntry) o;
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