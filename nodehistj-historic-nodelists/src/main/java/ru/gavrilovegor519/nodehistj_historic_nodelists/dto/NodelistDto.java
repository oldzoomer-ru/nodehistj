package ru.gavrilovegor519.nodehistj_historic_nodelists.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.gavrilovegor519.nodelistj.enums.Keywords;

import java.util.List;
import java.util.Objects;

@Getter
@Builder
@ToString
public class NodelistDto {
    private Integer nodelistYear;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodelistDto that)) return false;
        return Objects.equals(nodelistYear, that.nodelistYear) &&
               Objects.equals(nodelistName, that.nodelistName) &&
               Objects.equals(zone, that.zone) &&
               Objects.equals(network, that.network) &&
               Objects.equals(node, that.node) &&
               keywords == that.keywords &&
               Objects.equals(nodeName, that.nodeName) &&
               Objects.equals(location, that.location) &&
               Objects.equals(sysOpName, that.sysOpName) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(baudRate, that.baudRate) &&
               Objects.equals(flags, that.flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodelistYear, nodelistName, zone, network, node,
                keywords, nodeName, location, sysOpName, phone, baudRate, flags);
    }
}
