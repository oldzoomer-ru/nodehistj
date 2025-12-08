package ru.oldzoomer.nodehistj_history_diff.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeHistoryEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeHistoryEntryRepository;
import ru.oldzoomer.nodehistj_history_diff.repo.NodelistEntryRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Component for processing differences between nodelist versions.
 * Identifies added, removed, and modified nodes between consecutive nodelist versions.
 * Creates history entries for these changes in the database.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class NodelistDiffProcessor {
    private final NodelistEntryRepository nodelistEntryRepository;
    private final NodeHistoryEntryRepository nodeHistoryEntryRepository;

    @Value("${app.diff.fetch.size}")
    private int fetchSize;

    private NodelistEntry newNodelist;

    /**
     * Processes differences between all available nodelist versions.
     * Compares consecutive nodelist versions in reverse order (newest to oldest)
     * to ensure correct comparison direction.
     */
    @Transactional
    public void processNodelistDiffs() {
        try {
            log.info("Processing nodelist diffs...");

            Slice<NodelistEntry> nodeListEntries = nodelistEntryRepository
                    .findAll(PageRequest.of(0, fetchSize,
                            Sort.by(Sort.Direction.DESC, "nodelistYear", "nodelistName")));

            // Check if there are at least two nodelist versions to compare
            if (nodeListEntries.getNumberOfElements() < 2) {
                log.info("Not enough nodelist versions to compare");
                return;
            }

            nodeHistoryEntryRepository.deleteAll(); // Clear existing history data before processing new diffs

            // Process differences between consecutive nodelists in reverse order
            // (from newest to oldest to ensure correct comparison)
            processNodelistEntriesSlice(nodeListEntries);

            // Process remaining nodelist entries if any
            while (nodeListEntries.hasNext()) {
                nodeListEntries = nodelistEntryRepository.findAll(nodeListEntries.nextPageable());
                processNodelistEntriesSlice(nodeListEntries);
            }

            log.info("Nodelist diff processing completed");

        } catch (Exception e) {
            log.error("Error processing nodelist diffs", e);
        }
    }

    /**
     * Processes differences between consecutive nodelist entries in a slice.
     *
     * @param nodeListEntries the slice of nodelist entries to process
     */
    private void processNodelistEntriesSlice(Slice<NodelistEntry> nodeListEntries) {
        for (int i = 0; i < nodeListEntries.getNumberOfElements(); i++) {
            NodelistEntry oldNodelist = newNodelist;
            newNodelist = nodeListEntries.getContent().get(i);
            if (oldNodelist != null) {
                processDiffBetweenNodelists(oldNodelist, newNodelist);
            }
        }
    }

    /**
     * Processes differences between two specific nodelist versions.
     *
     * @param oldNodelist the previous nodelist version
     * @param newNodelist the current nodelist version
     */
    private void processDiffBetweenNodelists(NodelistEntry oldNodelist, NodelistEntry newNodelist) {
        Integer prevYear = oldNodelist.getNodelistYear();
        String prevName = oldNodelist.getNodelistName();
        Integer currYear = newNodelist.getNodelistYear();
        String currName = newNodelist.getNodelistName();

        log.info("Processing diff between {}/{} and {}/{}", prevYear, prevName, currYear, currName);

        // Get nodes from both nodelists
        Set<NodeEntry> previousNodes = oldNodelist.getNodeEntries();
        Set<NodeEntry> currentNodes = newNodelist.getNodeEntries();

        // Create maps for easier lookup
        Map<String, NodeEntry> previousNodeMap = previousNodes.stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node, (a, b) -> a));
        Map<String, NodeEntry> currentNodeMap = currentNodes.stream()
                .collect(Collectors.toMap(this::getNodeKey, node -> node, (a, b) -> a));

        LocalDate changeDate = parseNodelistDate(currYear, currName);

        // Find added nodes
        for (NodeEntry currentNode : currentNodes) {
            String key = getNodeKey(currentNode);
            if (!previousNodeMap.containsKey(key)) {
                createHistoryEntry(currentNode, currYear, currName, changeDate,
                        NodeHistoryEntry.ChangeType.ADDED, null);
            }
        }

        // Find removed nodes
        for (NodeEntry previousNode : previousNodes) {
            String key = getNodeKey(previousNode);
            if (!currentNodeMap.containsKey(key)) {
                createHistoryEntry(previousNode, currYear, currName, changeDate,
                        NodeHistoryEntry.ChangeType.REMOVED, null);
            }
        }

        // Find modified nodes
        for (NodeEntry currentNode : currentNodes) {
            String key = getNodeKey(currentNode);
            NodeEntry previousNode = previousNodeMap.get(key);
            if (previousNode != null && !nodesEqual(previousNode, currentNode)) {
                createHistoryEntry(currentNode, currYear, currName, changeDate,
                        NodeHistoryEntry.ChangeType.MODIFIED, previousNode);
            }
        }
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
     */
    private void createHistoryEntry(NodeEntry node, Integer nodelistYear,
                                    String nodelistName, LocalDate changeDate,
            NodeHistoryEntry.ChangeType changeType, NodeEntry previousNode) {
        NodeHistoryEntry historyEntry = new NodeHistoryEntry();

        historyEntry.setZone(node.getZone());
        historyEntry.setNetwork(node.getNetwork());
        historyEntry.setNode(node.getNode());
        historyEntry.setChangeDate(changeDate);
        historyEntry.setNodelistYear(nodelistYear);
        historyEntry.setNodelistName(nodelistName);
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

    /**
     * Parses the date from a nodelist name.
     *
     * @param year the year of the nodelist
     * @param nodelistName the name of the nodelist
     * @return the parsed LocalDate representing the nodelist date
     */
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