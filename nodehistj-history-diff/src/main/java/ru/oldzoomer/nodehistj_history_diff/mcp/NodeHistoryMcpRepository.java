package ru.oldzoomer.nodehistj_history_diff.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.oldzoomer.nodehistj_history_diff.dto.NodeHistoryEntryDto;
import ru.oldzoomer.nodehistj_history_diff.service.NodeHistoryService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class NodeHistoryMcpRepository {
    private final NodeHistoryService nodeHistoryService;

    @Tool(description = "Get node history by their 3D (node) address (eg. 2:5015/519). "
        + "Optional pagination: page (default 0), size (default 100)")
    public List<NodeHistoryEntryDto> getNodeHistoryByAddressAndYearAndDay(
        String address, Integer page, Integer size
    ) {
        int p = page != null ? page : 0;
        int s = size != null ? Math.min(size, 1000) : 100;
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)/(\\d+)").matcher(address);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid address format: " + address);
        }
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));
        int node = Integer.parseInt(matcher.group(3));

        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Order.desc("changeDate")));
        return nodeHistoryService.getNodeHistory(zone, network, node, pageable).toList();
    }

    @Tool(description = "Get network history by their 2D (zone:network) address (eg. 2:5015). "
        + "Optional pagination: page (default 0), size (default 100)")
    public List<NodeHistoryEntryDto> getNetworkHistory(
        String address, Integer page, Integer size
    ) {
        int p = page != null ? page : 0;
        int s = size != null ? Math.min(size, 1000) : 100;
        Matcher matcher = Pattern.compile("(\\d+):(\\d+)").matcher(address);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid address format: " + address);
        }
        int zone = Integer.parseInt(matcher.group(1));
        int network = Integer.parseInt(matcher.group(2));

        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Order.desc("changeDate")));
        return nodeHistoryService.getNetworkHistory(zone, network, pageable).toList();
    }

    @Tool(description = "Get zone history by their zone. "
        + "Optional pagination: page (default 0), size (default 100)")
    public List<NodeHistoryEntryDto> getZoneHistory(
        Integer zone, Integer page, Integer size
    ) {
        int p = page != null ? page : 0;
        int s = size != null ? Math.min(size, 1000) : 100;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Order.desc("changeDate")));
        return nodeHistoryService.getZoneHistory(zone, pageable).toList();
    }
}
