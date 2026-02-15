package ru.oldzoomer.nodehistj_newest_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.minio.utils.S3Utils;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodelistj.Nodelist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Year;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Component for processing and storing historical nodelists in the database.
 * Listens to Kafka topic for new nodelist files, downloads them from MinIO
 * storage, parses and saves to database, then clears Redis cache.
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
     *
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
     *
     * @param modifiedObjects list of MinIO object paths that were modified
     */
    @CacheEvict(value = "nodelistRequests", allEntries = true)
    @Transactional
    public void updateNodelist(List<String> modifiedObjects) {
        log.info("Update nodelists is started");

        String object = modifiedObjects.stream()
                .filter(x -> OBJECT_PATH_PATTERN.matcher(x).matches())
                .max(Comparator.naturalOrder())
                .orElseThrow();

        Matcher matcher = OBJECT_PATH_PATTERN.matcher(object);
        if (!matcher.matches()) {
            log.warn("Object {} is not a nodelist", object);
            return;
        }

        Year year = Year.parse(matcher.group(1));
        Integer dayOfYear = Integer.valueOf(matcher.group(2));

        if (nodelistEntryRepository.existsByNodelistYearAndDayOfYear(year.getValue(), dayOfYear)) {
            log.info("Nodelist {} from {} year is exist", dayOfYear, year);
            return;
        }

        try (InputStream inputStream = s3Utils.getObject(minioBucket, object)) {
            Nodelist nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
            nodelistEntryRepository.deleteAll(); // for disk space economy
            nodelistEntryRepository.save(updateNodelist(nodelist, year, dayOfYear));
        } catch (Exception e) {
            log.error("Failed to add nodelist to database", e);
        }

        log.info("Update nodelists is finished");
    }

    /**
     * Updates the database with entries from a single nodelist file. This
     * method processes the parsed nodelist object and updates the database with
     * the corresponding entries for the specified year and nodelist name.
     *
     * @param nodelist The parsed nodelist object containing node entries.
     * @param year The year of the nodelist.
     * @return nodelist entry
     */
    private NodelistEntry updateNodelist(Nodelist nodelist, Year year, Integer dayOfYear) {
        log.info("Update nodelist from {} year and name {} is started", year, dayOfYear);

        NodelistEntry nodelistEntryNew = new NodelistEntry();
        nodelistEntryNew.setNodelistYear(year.getValue());
        nodelistEntryNew.setDayOfYear(dayOfYear);

        for (ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry : nodelist.getNodelist()) {
            nodelistEntryNew.getNodeEntries().add(getNodeEntry(nodeListEntry));
        }

        return nodelistEntryNew;
    }
}
