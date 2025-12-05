package ru.oldzoomer.nodehistj_historic_nodelists.mcp;

import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HistoricNodelistMcpRepository {
    private final HistoricNodelistService historicNodelistService;

    @McpTool(description = "Get node data by their 3D (node) address (eg. 2:5015/519), year, and day of nodelist")
    public NodeEntryDto getNodeByAddressAndYearAndDay(String address, int year, int day) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)/(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));
        int node = Integer.parseInt(matcher.group(3));

        return historicNodelistService.getNodelistEntry(zone, network, node, year, day);
    }

    @McpTool(description = "Get list of node data by their 2D (zone:network) " +
            "address (eg. 2:5015), year, and day of nodelist")
    public Set<NodeEntryDto> getNodesByNetworkAndYearAndDay(String address, int year, int day) {
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)").matcher(address);
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));

        return historicNodelistService.getNodelistEntry(zone, network, year, day);
    }

    @McpTool(description = "Get list of node data by their zone, year, and day of nodelist")
    public Set<NodeEntryDto> getNodesByZoneAndYearAndDay(int zone, int year, int day) {
        return historicNodelistService.getNodelistEntry(zone, year, day);
    }
}
