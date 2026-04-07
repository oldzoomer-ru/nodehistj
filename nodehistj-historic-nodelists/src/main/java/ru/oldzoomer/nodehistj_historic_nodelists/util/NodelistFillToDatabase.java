package ru.oldzoomer.nodehistj_historic_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.minio.utils.S3Utils;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodelistj.Nodelist;

import java.io.IOException;
import java.io.InputStream;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Component for processing and storing historical nodelists in the database.
 * Listens to Kafka topic for new nodelist files, downloads them from MinIO storage,
 * parses and saves to database, then clears Redis cache.
 */
@RequiredArgsConstructor
@Component
@Log4j2
public class NodelistFillToDatabase {
    private final S3Utils s3Utils;
    private final NodelistEntryRepository nodelistEntryRepository;

    /**
     * S3 bucket name where nodelists are stored
     */
    @Value("${s3.bucket}")
    private String minioBucket;

    // Precompiled pattern to extract year and name from MinIO object path
    private static final Pattern OBJECT_PATH_PATTERN = Pattern.compile(".*/(\\d{4})/nodelist\\.(\\d{3})");

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
    @CacheEvict(value = "historicNodelistRequests", allEntries = true)
    @Transactional
    public void updateNodelist(List<String> modifiedObjects) {
        log.info("Update nodelists is started");

        for (String object : modifiedObjects) {
            try {
                NodelistFile nodelistFile = getAndCheckMetadata(object);

                try (InputStream inputStream = s3Utils.getObject(minioBucket, object)) {
                    Nodelist nodelist = new Nodelist(inputStream);
                    nodelistEntryRepository.save(updateNodelist(nodelist, nodelistFile.year, nodelistFile.dayOfYear));
                }
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage());
            } catch (IOException e) {
                throw new IllegalStateException(e);
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
     * @return nodelist entry
     */
    private NodelistEntry updateNodelist(Nodelist nodelist, Year year, int dayOfYear) {
        log.info("Update nodelist from {} year and name {} is started", year, dayOfYear);

        NodelistEntry nodelistEntryNew = new NodelistEntry();
        nodelistEntryNew.setNodelistYear(year.getValue());
        nodelistEntryNew.setDayOfYear(dayOfYear);

        for (ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry : nodelist.getNodelist()) {
            nodelistEntryNew.getNodeEntries().add(getNodeEntry(nodeListEntry));
        }

        return nodelistEntryNew;
    }

    /**
     * Checks if given S3 object correct and exists.
     *
     * @param object The S3 object to check.
     * @return The S3 object metadata if it exists and is correct, otherwise throws an exception.
     */
    private NodelistFile getAndCheckMetadata(String object) {
        log.debug("Checking S3 object {}", object);

        Matcher matcher = OBJECT_PATH_PATTERN.matcher(object);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Object %s is not nodelist", object));
        }

        Year year = Year.parse(matcher.group(1));
        int dayOfYear = Integer.parseInt(matcher.group(2));

        if (nodelistEntryRepository.existsByNodelistYearAndDayOfYear(year.getValue(), dayOfYear)) {
            throw new IllegalArgumentException(String.format("Nodelist %d from %s year is exist", dayOfYear, year));
        }

        return new NodelistFile(year, dayOfYear);
    }

    /**
     * Nodelist file metadata.
     *
     * @param year      The year of the nodelist file.
     * @param dayOfYear Day number in the year of the nodelist file.
     */
    private record NodelistFile(Year year, int dayOfYear) {
    }
}
