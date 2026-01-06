package ru.oldzoomer.nodehistj_history_diff.mcp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

@Component
@RequiredArgsConstructor
public class NodeHistoryMcpRepository {
    private final NodeHistoryService nodeHistoryService;

    @McpTool(description = "Get node history by their 3D (node) address (eg. 2:5015/519)")
    public List<NodeHistoryEntryDto> getNodeHistoryByAddressAndYearAndDay(String address) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)/(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));
        int node = Integer.parseInt(matcher.group(3));

        return nodeHistoryService.getNodeHistory(zone, network, node, Pageable.unpaged()).toList();
    }

    @McpTool(description = "Get network history by their 2D (zone:network) address (eg. 2:5015)")
    public List<NodeHistoryEntryDto> getNetworkHistory(String address) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));

        return nodeHistoryService.getNetworkHistory(zone, network, Pageable.unpaged()).toList();
    }

    @McpTool(description = "Get zone history by their zone")
    public List<NodeHistoryEntryDto> getZoneHistory(Integer zone) {
        return nodeHistoryService.getZoneHistory(zone, Pageable.unpaged()).toList();
    }
}
