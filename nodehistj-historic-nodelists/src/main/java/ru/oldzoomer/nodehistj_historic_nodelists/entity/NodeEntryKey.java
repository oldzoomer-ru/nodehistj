package ru.oldzoomer.nodehistj_historic_nodelists.entity;

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
 * - Nodelist year
 * - Nodelist name
 * - Zone identifier
 * - Network identifier
 * - Node identifier
 * <p>
 * This key is used to uniquely identify a node entry in the database.
 * <p>
 * The key structure follows Cassandra's data modeling best practices:
 * - Nodelist year and name are used as partition keys for efficient data distribution
 * - Zone, network, and node are used as clustering columns to
 *   organize data within partitions
 * <p>
 * This composite key allows for efficient querying of node entries by
 * different combinations of year, name, zone, network, and node.
 */
@PrimaryKeyClass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeEntryKey implements Serializable {

    @PrimaryKeyColumn(name = "nodelist_year", type = PrimaryKeyType.PARTITIONED)
    private Integer nodelistYear;

    @PrimaryKeyColumn(name = "nodelist_name", type = PrimaryKeyType.PARTITIONED)
    private String nodelistName;

    @PrimaryKeyColumn(name = "zone", type = PrimaryKeyType.CLUSTERED)
    private Integer zone;

    @PrimaryKeyColumn(name = "network", type = PrimaryKeyType.CLUSTERED)
    private Integer network;

    @PrimaryKeyColumn(name = "node", type = PrimaryKeyType.CLUSTERED)
    private Integer node;

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