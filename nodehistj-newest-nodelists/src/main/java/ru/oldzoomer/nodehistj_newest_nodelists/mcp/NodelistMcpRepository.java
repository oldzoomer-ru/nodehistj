package ru.oldzoomer.nodehistj_newest_nodelists.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodelistMcpRepository {
    private final NodelistService nodelistService;

    @Tool(description = "Get node data by their 3D (zone:network/node) address (eg. 2:5015/519)")
    public NodeEntryDto getNodeByAddress(int zone, int network, int node) {
        return nodelistService.getNodelistEntry(zone, network, node);
    }

    @Tool(description = "Get list of node data by their 2D (zone:network) address (eg. 2:5015)")
    public Set<NodeEntryDto> getNodesByNetwork(int zone, int network) {
        return nodelistService.getNodelistEntry(zone, network);
    }

    @Tool(description = "Get list of node data by their zone")
    public Set<NodeEntryDto> getNodesByZone(int zone) {
        return nodelistService.getNodelistEntry(zone);
    }
}
