package ru.oldzoomer.nodehistj_newest_nodelists.dto;

import ru.oldzoomer.nodelistj.enums.Keywords;

import java.util.List;

/**
 * Test data builder for {@link NodeEntryDto}.
 * Provides sensible defaults and fluent {@code with*} methods for customization.
 */
public final class NodeEntryDtoBuilder {

    private Integer zone = 1;
    private Integer network = 1;
    private Integer node = 1;
    private Keywords keywords = Keywords.ZONE;
    private String nodeName = "TestNode";
    private String location = "TestLocation";
    private String sysOpName = "TestSysOp";
    private String phone = "123-456-7890";
    private Integer baudRate = 9600;
    private List<String> flags = List.of();

    public static NodeEntryDtoBuilder aNodeEntryDto() {
        return new NodeEntryDtoBuilder();
    }

    public NodeEntryDtoBuilder withZone(Integer zone) {
        this.zone = zone;
        return this;
    }

    public NodeEntryDtoBuilder withNetwork(Integer network) {
        this.network = network;
        return this;
    }

    public NodeEntryDtoBuilder withNode(Integer node) {
        this.node = node;
        return this;
    }

    public NodeEntryDtoBuilder withKeywords(Keywords keywords) {
        this.keywords = keywords;
        return this;
    }

    public NodeEntryDtoBuilder withNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public NodeEntryDtoBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public NodeEntryDtoBuilder withSysOpName(String sysOpName) {
        this.sysOpName = sysOpName;
        return this;
    }

    public NodeEntryDtoBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public NodeEntryDtoBuilder withBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public NodeEntryDtoBuilder withFlags(List<String> flags) {
        this.flags = flags;
        return this;
    }

    public NodeEntryDto build() {
        return new NodeEntryDto(
            zone, network, node, keywords, nodeName,
            location, sysOpName, phone, baudRate, flags
        );
    }
}
