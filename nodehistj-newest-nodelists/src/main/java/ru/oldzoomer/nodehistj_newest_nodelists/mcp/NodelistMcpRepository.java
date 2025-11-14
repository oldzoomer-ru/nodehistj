package ru.oldzoomer.nodehistj_newest_nodelists.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_newest_nodelists.service.NodelistService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class NodelistMcpRepository {
    private final NodelistService nodelistService;

    @Tool(description = "Get node data by their 3D (node) address (eg. 2:5015/519)")
    public NodeEntryDto getNodeByAddress(String address) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)/(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));
        int node = Integer.parseInt(matcher.group(3));

        return nodelistService.getNodelistEntry(zone, network, node);
    }

    @Tool(description = "Get list of node data by their 2D (zone:network) address (eg. 2:5015)")
    public List<NodeEntryDto> getNodesByNetwork(String address) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));

        return nodelistService.getNodelistEntry(zone, network);
    }

    @Tool(description = "Get list of node data by their zone")
    public List<NodeEntryDto> getNodesByZone(int zone) {
        return nodelistService.getNodelistEntry(zone);
    }
}
