package ru.oldzoomer.nodehistj_history_diff.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;
import ru.oldzoomer.redis.utils.ClearRedisCache;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Log4j2
@Profile("!test")
public class NodelistDiffProcessor {
    private final NodeEntryRepository nodeEntryRepository;
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;
    private final ClearRedisCache clearRedisCache;

    public void processNodelistDiffs(List<String> modifiedObjects) {
        log.info("Processing nodelist diffs for {} objects", modifiedObjects.size());

        try {
            // Get all nodelist versions sorted by date
            List<Object[]> nodelistVersions = nodeEntryRepository.findAllNodelistVersions();

            if (nodelistVersions.size() < 2) {
                log.info("Not enough nodelist versions to compare");
                return;
            }

            // Process differences between consecutive nodelists
            for (int i = 0; i < nodelistVersions.size() - 1; i++) {
                Object[] current = nodelistVersions.get(i);
                Object[] previous = nodelistVersions.get(i + 1);

                Integer currentYear = (Integer) current[0];
                String currentName = (String) current[1];
                Integer previousYear = (Integer) previous[0];
                String previousName = (String) previous[1];

                processDiffBetweenNodelists(previousYear, previousName, currentYear, currentName);
            }

            clearRedisCache.clearCache();
            log.info("Nodelist diff processing completed");

        } catch (Exception e) {
            log.error("Error processing nodelist diffs", e);
        }
    }

    @Transactional
    private void processDiffBetweenNodelists(Integer prevYear, String prevName,
                                             Integer currYear, String currName) {
        log.info("Processing diff between {}/{} and {}/{}", prevYear, prevName, currYear, currName);

        // Get nodes from both nodelists
        List<NodeEntry> previousNodes = nodeEntryRepository.findByNodelistYearAndName(prevYear, prevName);
        List<NodeEntry> currentNodes = nodeEntryRepository.findByNodelistYearAndName(currYear, currName);

        // Create maps for easier lookup
        Map<String, NodeEntry> previousNodeMap = previousNodes.stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node));
        Map<String, NodeEntry> currentNodeMap = currentNodes.stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node));

        LocalDate changeDate = parseNodelistDate(currYear, currName);

        // Find added nodes
        for (NodeEntry currentNode : currentNodes) {
            String key = getNodeKey(currentNode);
            if (!previousNodeMap.containsKey(key)) {
                createHistoryEntry(currentNode, changeDate, NodeHistoryEntry.ChangeType.ADDED, null);
            }
        }

        // Find removed nodes
        for (NodeEntry previousNode : previousNodes) {
            String key = getNodeKey(previousNode);
            if (!currentNodeMap.containsKey(key)) {
                createHistoryEntry(previousNode, changeDate, NodeHistoryEntry.ChangeType.REMOVED, null);
            }
        }

        // Find modified nodes
        for (NodeEntry currentNode : currentNodes) {
            String key = getNodeKey(currentNode);
            NodeEntry previousNode = previousNodeMap.get(key);
            if (previousNode != null && !nodesEqual(previousNode, currentNode)) {
                createHistoryEntry(currentNode, changeDate, NodeHistoryEntry.ChangeType.MODIFIED, previousNode);
            }
        }
    }

    private String getNodeKey(NodeEntry node) {
        return String.format("%d:%d:%d", node.getZone(), node.getNetwork(), node.getNode());
    }

    private boolean nodesEqual(NodeEntry node1, NodeEntry node2) {
        return Objects.equals(node1.getKeywords(), node2.getKeywords()) &&
                Objects.equals(node1.getNodeName(), node2.getNodeName()) &&
                Objects.equals(node1.getLocation(), node2.getLocation()) &&
                Objects.equals(node1.getSysOpName(), node2.getSysOpName()) &&
                Objects.equals(node1.getPhone(), node2.getPhone()) &&
                Objects.equals(node1.getBaudRate(), node2.getBaudRate()) &&
                Objects.equals(node1.getFlags(), node2.getFlags());
    }

    private void createHistoryEntry(NodeEntry node, LocalDate changeDate,
                                    NodeHistoryEntry.ChangeType changeType, NodeEntry previousNode) {
        NodeHistoryEntry historyEntry = new NodeHistoryEntry();

        historyEntry.setZone(node.getZone());
        historyEntry.setNetwork(node.getNetwork());
        historyEntry.setNode(node.getNode());
        historyEntry.setChangeDate(changeDate);
        historyEntry.setNodelistYear(node.getNodelistEntry().getNodelistYear());
        historyEntry.setNodelistName(node.getNodelistEntry().getNodelistName());
        historyEntry.setChangeType(changeType);

        // Current values
        historyEntry.setKeywords(node.getKeywords());
        historyEntry.setNodeName(node.getNodeName());
        historyEntry.setLocation(node.getLocation());
        historyEntry.setSysOpName(node.getSysOpName());
        historyEntry.setPhone(node.getPhone());
        historyEntry.setBaudRate(node.getBaudRate());
        historyEntry.setFlags(node.getFlags());

        // Previous values (for MODIFIED entries)
        if (previousNode != null && changeType == NodeHistoryEntry.ChangeType.MODIFIED) {
            historyEntry.setPrevKeywords(previousNode.getKeywords());
            historyEntry.setPrevNodeName(previousNode.getNodeName());
            historyEntry.setPrevLocation(previousNode.getLocation());
            historyEntry.setPrevSysOpName(previousNode.getSysOpName());
            historyEntry.setPrevPhone(previousNode.getPhone());
            historyEntry.setPrevBaudRate(previousNode.getBaudRate());
            historyEntry.setPrevFlags(previousNode.getFlags());
        }

        nodeHistoryEntryRepository.save(historyEntry);
    }

    private LocalDate parseNodelistDate(Integer year, String nodelistName) {
        // Extract day of year from nodelist name (e.g., "nodelist.001" -> day 1)
        Pattern pattern = Pattern.compile("nodelist\\.(\\d{3})");
        Matcher matcher = pattern.matcher(nodelistName);

        if (matcher.matches()) {
            int dayOfYear = Integer.parseInt(matcher.group(1));
            return LocalDate.ofYearDay(year, dayOfYear);
        }

        // Fallback to current date if parsing fails
        return LocalDate.now();
    }
}