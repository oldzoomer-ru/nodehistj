package ru.oldzoomer.nodehistj_historic_nodelists.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import ru.oldzoomer.nodehistj_historic_nodelists.dto.NodeEntryDto;
import ru.oldzoomer.nodehistj_historic_nodelists.service.HistoricNodelistService;

import java.time.Year;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class HistoricNodelistMcpRepository {
    private final HistoricNodelistService historicNodelistService;

    @Tool(description = """
            Get node data by their 3D (zone:network/node)
            address (eg. 2:5015/519), year, and day of nodelist
            """)
    public NodeEntryDto getNodeByAddressAndYearAndDay(int zone, int network, int node, Year year, int day) {
        return historicNodelistService.getNodelistEntry(year, day, zone, network, node);
    }

    @Tool(description = """
            Get list of node data by their 2D (zone:network)
            address (eg. 2:5015), year, and day of nodelist
            """)
    public Set<NodeEntryDto> getNodesByNetworkAndYearAndDay(int zone, int network, Year year, int day) {
        return historicNodelistService.getNodelistEntry(year, day, zone, network);
    }

    @Tool(description = "Get list of node data by their zone, year, and day of nodelist")
    public Set<NodeEntryDto> getNodesByZoneAndYearAndDay(int zone, Year year, Integer day) {
        return historicNodelistService.getNodelistEntry(year, day, zone);
    }
}
