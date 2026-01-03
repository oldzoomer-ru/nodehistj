package ru.oldzoomer.nodehistj_history_diff.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.minio.utils.MinioUtils;
import ru.oldzoomer.nodehistj_history_diff.entity.NodeEntry;
import ru.oldzoomer.nodehistj_history_diff.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_history_diff.repo.NodelistEntryRepository;
import ru.oldzoomer.nodelistj.Nodelist;

/**
 * Component for processing and storing historical nodelists in the database.
 * Listens to Kafka topic for new nodelist files, downloads them from MinIO storage,
 * parses and saves to database, then clears Redis cache.
 */
@RequiredArgsConstructor
@Component
@Log4j2
public class NodelistFillToDatabase {
    private final MinioUtils minioUtils;
    private final NodelistEntryRepository nodelistEntryRepository;

    /** MinIO bucket name where nodelists are stored */
    @Value("${app.minio.bucket}")
    private String minioBucket;

    // Precompiled pattern to extract year and name from MinIO object path
    private static final Pattern OBJECT_PATH_PATTERN = Pattern.compile(".*/(\\d{4})/(nodelist\\.(\\d{3}))");

    /**
     * Converts nodelist entry from common format to database entity
     * @param nodeListEntry source nodelist entry from common library
     * @return populated NodeEntry entity ready for saving
     */
    private static NodeEntry getNodeEntry(
            ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry
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
        return nodeEntryNew;
    }

    /**
     * Kafka listener method triggered when new nodelists are downloaded.
     * Processes each modified nodelist file from MinIO storage.
     * @param modifiedObjects list of MinIO object paths that were modified
     */
    @CacheEvict(value = "nodelistHistory", allEntries = true)
    @Transactional
    public void updateNodelist(List<String> modifiedObjects) {
        log.info("Update nodelists is started");

        for (String object : modifiedObjects) {
            Matcher matcher = OBJECT_PATH_PATTERN.matcher(object);
            if (!matcher.matches()) {
                log.debug("Object {} is not a nodelist", object);
                continue;
            }

            int year = Integer.parseInt(matcher.group(1));
            String name = matcher.group(2);

            if (nodelistEntryRepository.existsByNodelistYearAndNodelistName(year, name)) {
                log.info("Nodelist {} from {} year is exist", name, year);
            }

            try (InputStream inputStream = minioUtils.getObject(minioBucket, object)) {
                Nodelist nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
                nodelistEntryRepository.save(updateNodelist(nodelist, year, name));
            } catch (Exception e) {
                log.error("Failed to add nodelist to database", e);
            }
        }

        log.info("Update nodelists is finished");
    }

    /**
     * Updates the database with entries from a single nodelist file.
     * This method processes the parsed nodelist object and updates the database
     * with the corresponding entries for the specified year and nodelist name.
     *
     * @param nodelist The parsed nodelist object containing node entries.
     * @param year The year of the nodelist.
     * @param name The name of the nodelist file.
     * @return nodelist entry
     */
    private NodelistEntry updateNodelist(Nodelist nodelist, Integer year, String name) {
        log.info("Update nodelist from {} year and name {} is started", year, name);

        NodelistEntry nodelistEntryNew = new NodelistEntry();
        nodelistEntryNew.setNodelistYear(year);
        nodelistEntryNew.setNodelistName(name);

        for (ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry : nodelist.getNodelist()) {
            nodelistEntryNew.getNodeEntries().add(getNodeEntry(nodeListEntry));
        }

        return nodelistEntryNew;
    }
}
