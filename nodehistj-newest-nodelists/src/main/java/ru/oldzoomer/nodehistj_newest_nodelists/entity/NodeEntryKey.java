package ru.oldzoomer.nodehistj_newest_nodelists.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for NodeEntry entity.
 * <p>
 * Represents the unique identifier for a node entry in the system, combining:
 * - Zone identifier
 * - Network identifier
 * - Node identifier
 * - Nodelist year
 * - Nodelist name
 * <p>
 * This key is used to uniquely identify a node entry in the database.
 */
@PrimaryKeyClass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeEntryKey implements Serializable {

    @PrimaryKeyColumn(name = "zone", type = PrimaryKeyType.PARTITIONED)
    private Integer zone;

    @PrimaryKeyColumn(name = "network", type = PrimaryKeyType.CLUSTERED)
    private Integer network;

    @PrimaryKeyColumn(name = "node", type = PrimaryKeyType.CLUSTERED)
    private Integer node;

    @PrimaryKeyColumn(name = "nodelist_year", type = PrimaryKeyType.CLUSTERED)
    private Integer nodelistYear;

    @PrimaryKeyColumn(name = "nodelist_name", type = PrimaryKeyType.CLUSTERED)
    private String nodelistName;

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeEntryKey that = (NodeEntryKey) o;
        return Objects.equals(nodelistYear, that.nodelistYear) &&
                Objects.equals(nodelistName, that.nodelistName) &&
                Objects.equals(zone, that.zone) &&
                Objects.equals(network, that.network) &&
                Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodelistYear, nodelistName, zone, network, node);
    }
}