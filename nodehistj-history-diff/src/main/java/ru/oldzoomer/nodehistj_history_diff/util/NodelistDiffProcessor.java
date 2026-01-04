package ru.oldzoomer.nodehistj_history_diff.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;
import ru.oldzoomer.nodehistj_history_diff.repo.NodelistEntryRepository;

/**
 * Component for processing differences between nodelist versions.
 * Identifies added, removed, and modified nodes between consecutive nodelist versions.
 * Creates history entries for these changes in the database.
 */
@RequiredArgsConstructor
@Component
@Log4j2
public class NodelistDiffProcessor {
    private final NodelistEntryRepository nodelistEntryRepository;
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;

    /**
     * Processes differences between all available nodelist versions.
     * Compares consecutive nodelist versions in reverse order (newest to oldest)
     * to ensure correct comparison direction.
     */
    @Transactional
    public void processNodelistDiffs() {
        try {
            log.info("Processing nodelist diffs...");

            // Check if there are at least two nodelist versions to compare
            if (nodelistEntryRepository.count() < 2) {
                log.info("Not enough nodelist versions to compare");
                return;
            }

            nodeHistoryEntryRepository.deleteAll();

            nodelistEntryRepository.findAllAsStreamWithSort()
                                    .gather(Gatherers.windowSliding(2))
                                    .map(window -> processDiffBetweenNodelists(window.get(1), window.get(0)))
                                    .flatMap(list -> list.stream())
                                    .forEach(nodeHistoryEntryRepository::save);

            log.info("Nodelist diff processing completed");

        } catch (Exception e) {
            log.error("Error processing nodelist diffs", e);
            throw e;
        }
    }

    /**
     * Processes differences between two specific nodelist versions.
     *
     * @param oldNodelist the previous nodelist version
     * @param newNodelist the current nodelist version
     */
    private List<NodeHistoryEntry> processDiffBetweenNodelists(NodelistEntry oldNodelist, NodelistEntry newNodelist) {
        Integer prevYear = oldNodelist.getNodelistYear();
        Integer prevDay = oldNodelist.getDayOfYear();
        Integer currYear = newNodelist.getNodelistYear();
        Integer currDay = newNodelist.getDayOfYear();

        log.info("Processing diff between {}/{} and {}/{}", prevYear, prevDay, currYear, currDay);

        // Create maps for easier lookup
        Map<String, NodeEntry> previousNodeMap = oldNodelist.getNodeEntries()
                .stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node, (a, b) -> a));
        Map<String, NodeEntry> currentNodeMap = newNodelist.getNodeEntries()
                .stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node, (a, b) -> a));

        List<NodeHistoryEntry> historyEntries = new ArrayList<>();

        try {
            LocalDate changeDate = LocalDate.ofYearDay(currYear, currDay);

            // Find added nodes
            for (NodeEntry currentNode : currentNodeMap.values()) {
                String key = getNodeKey(currentNode);
                if (!previousNodeMap.containsKey(key)) {
                    historyEntries.add(createHistoryEntry(currentNode, currYear, currDay, changeDate,
                            NodeHistoryEntry.ChangeType.ADDED, null));
                }
            }

            // Find removed nodes
            for (NodeEntry previousNode : previousNodeMap.values()) {
                String key = getNodeKey(previousNode);
                if (!currentNodeMap.containsKey(key)) {
                    historyEntries.add(createHistoryEntry(previousNode, currYear, currDay, changeDate,
                            NodeHistoryEntry.ChangeType.REMOVED, null));
                }
            }

            // Find modified nodes
            for (NodeEntry currentNode : currentNodeMap.values()) {
                String key = getNodeKey(currentNode);
                NodeEntry previousNode = previousNodeMap.get(key);
                if (previousNode != null && !nodesEqual(previousNode, currentNode)) {
                    historyEntries.add(createHistoryEntry(currentNode, currYear, currDay, changeDate,
                            NodeHistoryEntry.ChangeType.MODIFIED, previousNode));
                }
            }
        } catch (IllegalArgumentException e) {
            log.info("Nodelist is skipped: {}/{}", currYear, currDay);
        }

        return historyEntries;
    }

    /**
     * Generates a unique key for a node based on its zone, network, and node number.
     *
     * @param node the node entry
     * @return a unique key string in the format "zone:network/node"
     */
    private String getNodeKey(NodeEntry node) {
        return String.format("%d:%d/%d", node.getZone(), node.getNetwork(), node.getNode());
    }

    /**
     * Compares two nodes for equality based on their properties.
     *
     * @param node1 the first node to compare
     * @param node2 the second node to compare
     * @return true if the nodes are equal, false otherwise
     */
    private boolean nodesEqual(NodeEntry node1, NodeEntry node2) {
        return Objects.equals(node1.getKeywords(), node2.getKeywords()) &&
                Objects.equals(node1.getNodeName(), node2.getNodeName()) &&
                Objects.equals(node1.getLocation(), node2.getLocation()) &&
                Objects.equals(node1.getSysOpName(), node2.getSysOpName()) &&
                Objects.equals(node1.getPhone(), node2.getPhone()) &&
                Objects.equals(node1.getBaudRate(), node2.getBaudRate()) &&
                Objects.equals(node1.getFlags(), node2.getFlags());
    }

    /**
     * Creates a history entry for a node change.
     *
     * @param node the node entry
     * @param changeDate the date of the change
     * @param changeType the type of change (ADDED, REMOVED, MODIFIED)
     * @param previousNode the previous node state (for MODIFIED changes)
     * @return node history diff entry
     */
    private NodeHistoryEntry createHistoryEntry(NodeEntry node, Integer nodelistYear,
                                    Integer dayOfYear, LocalDate changeDate,
            NodeHistoryEntry.ChangeType changeType, NodeEntry previousNode) {
        NodeHistoryEntry historyEntry = new NodeHistoryEntry();

        historyEntry.setZone(node.getZone());
        historyEntry.setNetwork(node.getNetwork());
        historyEntry.setNode(node.getNode());
        historyEntry.setChangeDate(changeDate);
        historyEntry.setNodelistYear(nodelistYear);
        historyEntry.setDayOfYear(dayOfYear);
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

        return historyEntry;
    }
}