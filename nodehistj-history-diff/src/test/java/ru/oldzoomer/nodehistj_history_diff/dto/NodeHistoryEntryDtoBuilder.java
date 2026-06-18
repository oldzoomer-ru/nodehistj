package ru.oldzoomer.nodehistj_history_diff.dto;

import ru.oldzoomer.nodelistj.enums.Keywords;

import java.time.LocalDate;
import java.util.List;

/**
 * Test data builder for {@link NodeHistoryEntryDto}.
 * Provides sensible defaults and fluent {@code with*} methods for customization.
 */
public final class NodeHistoryEntryDtoBuilder {

    private Integer zone = 1;
    private Integer network = 1;
    private Integer node = 1;
    private LocalDate changeDate = LocalDate.of(2024, 1, 1);
    private Integer nodelistYear = 2024;
    private Integer dayOfYear = 1;
    private NodeHistoryEntryDto.ChangeType changeType = NodeHistoryEntryDto.ChangeType.ADDED;
    private Keywords keywords = Keywords.ZONE;
    private String nodeName = "TestNode";
    private String location = "TestLocation";
    private String sysOpName = "TestSysOp";
    private String phone = "123-456-7890";
    private Integer baudRate = 9600;
    private List<String> flags = List.of();
    private Keywords prevKeywords = null;
    private String prevNodeName = null;
    private String prevLocation = null;
    private String prevSysOpName = null;
    private String prevPhone = null;
    private Integer prevBaudRate = null;
    private List<String> prevFlags = null;

    public static NodeHistoryEntryDtoBuilder aNodeHistoryEntryDto() {
        return new NodeHistoryEntryDtoBuilder();
    }

    public NodeHistoryEntryDtoBuilder withZone(Integer zone) {
        this.zone = zone;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withNetwork(Integer network) {
        this.network = network;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withNode(Integer node) {
        this.node = node;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withNodelistYear(Integer nodelistYear) {
        this.nodelistYear = nodelistYear;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withDayOfYear(Integer dayOfYear) {
        this.dayOfYear = dayOfYear;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withChangeType(NodeHistoryEntryDto.ChangeType changeType) {
        this.changeType = changeType;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withKeywords(Keywords keywords) {
        this.keywords = keywords;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withSysOpName(String sysOpName) {
        this.sysOpName = sysOpName;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withFlags(List<String> flags) {
        this.flags = flags;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPrevKeywords(Keywords prevKeywords) {
        this.prevKeywords = prevKeywords;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPrevNodeName(String prevNodeName) {
        this.prevNodeName = prevNodeName;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPrevLocation(String prevLocation) {
        this.prevLocation = prevLocation;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPrevSysOpName(String prevSysOpName) {
        this.prevSysOpName = prevSysOpName;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPrevPhone(String prevPhone) {
        this.prevPhone = prevPhone;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPrevBaudRate(Integer prevBaudRate) {
        this.prevBaudRate = prevBaudRate;
        return this;
    }

    public NodeHistoryEntryDtoBuilder withPrevFlags(List<String> prevFlags) {
        this.prevFlags = prevFlags;
        return this;
    }

    public NodeHistoryEntryDto build() {
        return new NodeHistoryEntryDto(
            zone, network, node, changeDate, nodelistYear, dayOfYear, changeType,
            keywords, nodeName, location, sysOpName, phone, baudRate, flags,
            prevKeywords, prevNodeName, prevLocation, prevSysOpName, prevPhone,
            prevBaudRate, prevFlags
        );
    }
}
