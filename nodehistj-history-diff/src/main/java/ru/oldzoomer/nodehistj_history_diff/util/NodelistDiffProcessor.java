package ru.oldzoomer.nodehistj_history_diff.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;

@RequiredArgsConstructor
@Component
@Slf4j
public class NodelistDiffProcessor {
    private final NodeEntryRepository nodeEntryRepository;
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;

    public synchronized void processNodelistDiffs() {
        try {
            nodeHistoryEntryRepository.deleteAll(); // Clear existing history data before processing new diffs

            // Get all nodelist versions sorted by date (newest first)
            List<Object[]> nodelistVersions = nodeEntryRepository.findAllNodelistVersions();

            if (nodelistVersions.size() < 2) {
                log.info("Not enough nodelist versions to compare");
                return;
            }

            // Process differences between consecutive nodelists in reverse order
            // (from newest to oldest to ensure correct comparison)
            for (int i = nodelistVersions.size() - 1; i > 0; i--) {
                Object[] older = nodelistVersions.get(i);
                Object[] newer = nodelistVersions.get(i - 1);

                Integer olderYear = (Integer) older[0];
                String olderName = (String) older[1];
                Integer newerYear = (Integer) newer[0];
                String newerName = (String) newer[1];

                processDiffBetweenNodelists(olderYear, olderName, newerYear, newerName);
            }

            log.info("Nodelist diff processing completed");

        } catch (Exception e) {
            log.error("Error processing nodelist diffs", e);
        }
    }

    private void processDiffBetweenNodelists(Integer prevYear, String prevName,
            Integer currYear, String currName) {
        log.info("Processing diff between {}/{} and {}/{}", prevYear, prevName, currYear, currName);

        // Get nodes from both nodelists
        List<NodeEntry> previousNodes = nodeEntryRepository.findByNodelistYearAndName(prevYear, prevName);
        List<NodeEntry> currentNodes = nodeEntryRepository.findByNodelistYearAndName(currYear, currName);

        // Create maps for easier lookup
        Map<String, NodeEntry> previousNodeMap = previousNodes.stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node, (a, b) -> a));
        Map<String, NodeEntry> currentNodeMap = currentNodes.stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node, (a, b) -> a));

        // Find added nodes
        for (NodeEntry currentNode : currentNodes) {
            String key = getNodeKey(currentNode);
            if (!previousNodeMap.containsKey(key)) {
                createHistoryEntry(currentNode, NodeHistoryEntry.ChangeType.ADDED, null);
            }
        }

        // Find removed nodes
        for (NodeEntry previousNode : previousNodes) {
            String key = getNodeKey(previousNode);
            if (!currentNodeMap.containsKey(key)) {
                createHistoryEntry(previousNode, NodeHistoryEntry.ChangeType.REMOVED, null);
            }
        }

        // Find modified nodes
        for (NodeEntry currentNode : currentNodes) {
            String key = getNodeKey(currentNode);
            NodeEntry previousNode = previousNodeMap.get(key);
            if (previousNode != null && !nodesEqual(previousNode, currentNode)) {
                createHistoryEntry(currentNode, NodeHistoryEntry.ChangeType.MODIFIED, previousNode);
            }
        }
    }

    private String getNodeKey(NodeEntry node) {
        return String.format("%d:%d/%d", node.getId().getZone(),
                node.getId().getNetwork(), node.getId().getNode());
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

    private void createHistoryEntry(NodeEntry node,
            NodeHistoryEntry.ChangeType changeType, NodeEntry previousNode) {
        // Check if similar entry already exists
        boolean entryExists = nodeHistoryEntryRepository.existsByZoneAndNetworkAndNode(
                node.getId().getZone(),
                node.getId().getNetwork(),
                node.getId().getNode(),
                node.getId().getNodelistYear(),
                node.getId().getNodelistName());

        if (entryExists) {
            return;
        }

        NodeHistoryEntry historyEntry = new NodeHistoryEntry();

        historyEntry.getId().setZone(node.getId().getZone());
        historyEntry.getId().setNetwork(node.getId().getNetwork());
        historyEntry.getId().setNode(node.getId().getNode());
        historyEntry.getId().setNodelistYear(node.getId().getNodelistYear());
        historyEntry.getId().setNodelistName(node.getId().getNodelistName());
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
}