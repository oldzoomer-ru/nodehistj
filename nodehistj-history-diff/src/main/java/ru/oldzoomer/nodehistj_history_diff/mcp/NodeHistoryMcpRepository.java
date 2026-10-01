package ru.oldzoomer.nodehistj_history_diff.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NodeHistoryMcpRepository {
    private final NodeHistoryService nodeHistoryService;

    @Tool(description = "Get node history by their 3D (zone:network/node) address (eg. 2:5015/519)")
    public List<NodeHistoryEntryDto> getNodeHistoryByAddressAndYearAndDay(int zone, int network, int node,
                                                                          int page, int pageSize) {
        return nodeHistoryService.getNodeHistory(zone, network, node, PageRequest.of(page, Math.min(pageSize, 100)))
                .toList();
    }

    @Tool(description = "Get network history by their 2D (zone:network) address (eg. 2:5015)")
    public List<NodeHistoryEntryDto> getNetworkHistory(int zone, int network,
                                                       int page, int pageSize) {
        return nodeHistoryService.getNetworkHistory(zone, network, PageRequest.of(page, Math.min(pageSize, 100)))
                .toList();
    }

    @Tool(description = "Get zone history by their zone")
    public List<NodeHistoryEntryDto> getZoneHistory(int zone, int page, int pageSize) {
        return nodeHistoryService.getZoneHistory(zone, PageRequest.of(page, Math.min(pageSize, 100))).toList();
    }
}
