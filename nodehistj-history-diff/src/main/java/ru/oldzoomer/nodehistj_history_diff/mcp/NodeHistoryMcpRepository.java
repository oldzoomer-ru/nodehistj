package ru.oldzoomer.nodehistj_history_diff.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class NodeHistoryMcpRepository {
    private final NodeHistoryService nodeHistoryService;

    @Tool(description = "Get node history by their 3D (node) address (eg. 2:5015/519)")
    public Page<NodeHistoryEntryDto> getNodeHistoryByAddressAndYearAndDay(String address) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)/(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));
        int node = Integer.parseInt(matcher.group(3));

        return nodeHistoryService.getNodeHistory(zone, network, node, Pageable.unpaged());
    }

    @Tool(description = "Get network history by their 2D (zone:network) address (eg. 2:5015)")
    public Page<NodeHistoryEntryDto> getNetworkHistory(String address) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));

        return nodeHistoryService.getNetworkHistory(zone, network, Pageable.unpaged());
    }

    @Tool(description = "Get zone history by their zone")
    public Page<NodeHistoryEntryDto> getZoneHistory(int zone) {
        return nodeHistoryService.getZoneHistory(zone, Pageable.unpaged());
    }
}
