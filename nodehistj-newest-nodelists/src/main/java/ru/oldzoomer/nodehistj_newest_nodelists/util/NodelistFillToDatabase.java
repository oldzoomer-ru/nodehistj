package ru.oldzoomer.nodehistj_newest_nodelists.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.minio.utils.MinioUtils;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.exception.NoNewObjects;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodelistj.Nodelist;
import ru.oldzoomer.redis.utils.ClearRedisCache;

/**
 * Component for processing and storing historical nodelists in the database.
 * Listens to Kafka topic for new nodelist files, downloads them from MinIO
 * storage,
 * parses and saves to database, then clears Redis cache.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class NodelistFillToDatabase {
    private final MinioUtils minioUtils;
    private final NodeEntryRepository nodeEntryRepository;
    private final NodelistEntryRepository nodelistEntryRepository;
    private final ClearRedisCache clearRedisCache;

    /** MinIO bucket name where nodelists are stored */
    @Value("${app.minio.bucket}")
    private String minioBucket;

    /**
     * Converts nodelist entry from common format to database entity
     * 
     * @param nodeListEntry    source nodelist entry from common library
     * @param nodelistEntryNew parent nodelist entry entity
     * @return populated NodeEntry entity ready for saving
     */
    private static NodeEntry getNodeEntry(
            ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry,
            ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry nodelistEntryNew) {
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
        nodeEntryNew.setNodelistEntry(nodelistEntryNew);
        return nodeEntryNew;
    }

    /**
     * Kafka listener method triggered when new nodelists are downloaded.
     * Processes each modified nodelist file from MinIO storage.
     * 
     * @param modifiedObjects list of MinIO object paths that were modified
     */
    public void updateNodelist(List<String> modifiedObjects) {
        log.info("Update nodelists is started");

        String modifiedObject = modifiedObjects.stream()
                .filter(object -> object.matches(".*/\\d{4}/nodelist\\.\\d{3}"))
                .sorted(Comparator.reverseOrder())
                .findFirst()
                .orElseThrow(NoNewObjects::new);

        log.info("New object: {}", modifiedObject);

        try (InputStream inputStream = minioUtils.getObject(minioBucket, modifiedObject)) {
            Matcher matcher = Pattern.compile(".*/(\\d{4})/(nodelist\\.\\d{3})").matcher(modifiedObject);
            matcher.find(); // Initializes the matcher
            Nodelist nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));

            log.info("Starting updating nodelist {} in database", modifiedObject);
            nodeEntryRepository.deleteAll();
            nodelistEntryRepository.deleteAll();
            updateNodelist(nodelist, Integer.parseInt(matcher.group(1)), matcher.group(2));
            log.info("Nodelist {} is added to database", modifiedObject);

            log.info("Clearing redis cache");
            clearRedisCache.clearCache();
            log.info("Redis cache is cleared");
        } catch (Exception e) {
            log.error("Failed to add nodelist to database", e);
        }

        log.info("Update nodelists is finished");
    }

    /**
     * Updates the database with entries from a single nodelist file.
     * This method processes the parsed nodelist object and updates the database
     * with the corresponding entries for the specified year and nodelist name.
     *
     * @param nodelist The parsed nodelist object containing node entries.
     * @param year     The year of the nodelist.
     * @param name     The name of the nodelist file.
     */
    @Transactional
    private void updateNodelist(Nodelist nodelist, Integer year, String name) {
        if (!nodelistEntryRepository.existsByNodelistYearAndNodelistName(year, name)) {
            log.info("Update nodelist from {} year and name \"{}\" is started", year, name);

            NodelistEntry nodelistEntryNew = new NodelistEntry();
            nodelistEntryNew.setNodelistYear(year);
            nodelistEntryNew.setNodelistName(name);
            nodelistEntryRepository.save(nodelistEntryNew);

            for (ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry : nodelist.getNodelist()) {
                nodeEntryRepository.save(getNodeEntry(nodeListEntry, nodelistEntryNew));
            }
            log.info("Update nodelist from {} year and name \"{}\" is finished", year, name);
        }
    }
}
