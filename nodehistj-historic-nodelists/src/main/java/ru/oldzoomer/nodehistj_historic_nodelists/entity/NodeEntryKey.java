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