package ru.oldzoomer.nodehistj_history_diff.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.minio.utils.MinioUtils;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodeEntryRepository;
import ru.oldzoomer.nodelistj.Nodelist;
import ru.oldzoomer.redis.utils.ClearRedisCache;

/**
 * Component for processing and storing historical nodelists in the database.
 * Listens to Kafka topic for new nodelist files, downloads them from MinIO storage,
 * parses and saves to database, then clears Redis cache.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class NodelistFillToDatabase {
    private final MinioUtils minioUtils;
    private final NodeEntryRepository nodeEntryRepository;
    private final ClearRedisCache clearRedisCache;
    private final NodelistDiffProcessor nodelistDiffProcessor;

    /** MinIO bucket name where nodelists are stored */
    @Value("${app.minio.bucket}")
    private String minioBucket;

    /**
     * Converts nodelist entry from common format to database entity
     * @param nodeListEntry source nodelist entry from common library
     * @param nodelistEntryNew parent nodelist entry entity
     * @return populated NodeEntry entity ready for saving
     */
    private static NodeEntry getNodeEntry(
        ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry,
        Integer year, String name
    ) {
        NodeEntry nodeEntryNew = new NodeEntry();

        nodeEntryNew.setZone(nodeListEntry.zone());
        nodeEntryNew.setNetwork(nodeListEntry.network());
        nodeEntryNew.setNode(nodeListEntry.node());
        nodeEntryNew.setBaudRate(nodeListEntry.baudRate());
        nodeEntryNew.setKeywords(nodeListEntry.keywords());
        nodeEntryNew.setLocation(nodeListEntry.location());
        nodeEntryNew.setNodeName(nodeListEntry.nodeName());
        nodeEntryNew.setPhone(nodeListEntry.phone());
        nodeEntryNew.setSysOpName(nodeListEntry.sysOpName());
        nodeEntryNew.setFlags(Arrays.asList(nodeListEntry.flags()));
        nodeEntryNew.setNodelistYear(year);
        nodeEntryNew.setNodelistName(name);
        return nodeEntryNew;
    }

    /**
     * Kafka listener method triggered when new nodelists are downloaded.
     * Processes each modified nodelist file from MinIO storage.
     * @param modifiedObjects list of MinIO object paths that were modified
     */
    public void updateNodelist(List<String> modifiedObjects) {
        log.info("Starting processing {} modified nodelists", modifiedObjects.size());
        
        List<String> validObjects = modifiedObjects.stream()
            .filter(object -> {
                Matcher matcher = Pattern.compile(".*/(\\d{4})/(nodelist\\.\\d{3})").matcher(object);
                boolean valid = matcher.find();
                if (!valid) {
                    log.debug("Skipping invalid nodelist object: {}", object);
                }
                return valid;
            })
            .toList();

        if (validObjects.isEmpty()) {
            log.warn("No valid nodelist objects found");
            return;
        }

        validObjects.forEach(object -> {
            try {
                processSingleNodelist(object);
            } catch (Exception e) {
                log.error("Failed to process nodelist {}", object, e);
            }
        });

        try {
            log.debug("Processing nodelist diffs");
            nodelistDiffProcessor.processNodelistDiffs();
            
            log.debug("Clearing Redis cache");
            clearRedisCache.clearCache();
        } catch (Exception e) {
            log.error("Error in post-processing steps", e);
        }
        
        log.info("Finished processing {} nodelists", validObjects.size());
    }

    private void processSingleNodelist(String object) throws Exception {
        Matcher matcher = Pattern.compile(".*/(\\d{4})/(nodelist\\.\\d{3})").matcher(object);
        matcher.find(); // Already validated
        
        log.debug("Downloading nodelist from MinIO: bucket={}, object={}", minioBucket, object);
        try (InputStream inputStream = minioUtils.getObject(minioBucket, object)) {
            byte[] fileContent = inputStream.readAllBytes();
            
            if (fileContent.length == 0) {
                log.warn("Empty nodelist file detected: {}", object);
                return;
            }
            
            Nodelist nodelist = new Nodelist(new ByteArrayInputStream(fileContent));
            int year = Integer.parseInt(matcher.group(1));
            String name = matcher.group(2);
            
            log.debug("Processing nodelist: year={}, name={}, size={} bytes", year, name, fileContent.length);
            updateNodelist(nodelist, year, name);
        } catch (Exception e) {
            log.error("Failed to process nodelist from MinIO (bucket={}, object={}): {}",
                     minioBucket, object, e.getMessage());
            throw e;
        }
    }

    /**
     * Updates the database with entries from a single nodelist file.
     * This method processes the parsed nodelist object and updates the database
     * with the corresponding entries for the specified year and nodelist name.
     *
     * @param nodelist The parsed nodelist object containing node entries.
     * @param year The year of the nodelist.
     * @param name The name of the nodelist file.
     */
    private void updateNodelist(Nodelist nodelist, Integer year, String name) {
        log.info("Processing nodelist from {} year and name \"{}\"", year, name);
        
        log.debug("Saving {} node entries", nodelist.getNodelist().size());
        List<NodeEntry> entries = nodelist.getNodelist().stream()
            .map(entry -> getNodeEntry(entry, year, name))
            .toList();
        
        nodeEntryRepository.saveAll(entries);
        log.info("Successfully processed nodelist from {} year ({} entries)", year, entries.size());
    }
}
