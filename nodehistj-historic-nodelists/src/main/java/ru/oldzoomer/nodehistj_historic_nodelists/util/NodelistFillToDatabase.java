package ru.oldzoomer.nodehistj_historic_nodelists.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.common.utils.ClearRedisCache;
import ru.oldzoomer.common.utils.MinioUtils;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_historic_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodelistj.Nodelist;

/**
 * Component for processing and storing historical nodelists in the database.
 * Listens to Kafka topic for new nodelist files, downloads them from MinIO storage,
 * parses and saves to database, then clears Redis cache.
 */
@RequiredArgsConstructor
@Component
@Log4j2
@Profile("!test")
public class NodelistFillToDatabase {
    private final MinioUtils minioUtils;
    private final NodeEntryRepository nodeEntryRepository;
    private final NodelistEntryRepository nodelistEntryRepository;
    private final ClearRedisCache clearRedisCache;

    /** MinIO storage path pattern for nodelist files */
    @Value("${minio.path}")
    private String minioPath;

    /** MinIO bucket name where nodelists are stored */
    @Value("${minio.bucket}")
    private String minioBucket;

    /**
     * Converts nodelist entry from common format to database entity
     * @param nodeListEntry source nodelist entry from common library
     * @param nodelistEntryNew parent nodelist entry entity
     * @return populated NodeEntry entity ready for saving
     */
    @NotNull
    private static NodeEntry getNodeEntry(
        ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry,
        NodelistEntry nodelistEntryNew
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
        nodeEntryNew.setNodelistEntry(nodelistEntryNew);
        return nodeEntryNew;
    }

    /**
     * Kafka listener method triggered when new nodelists are downloaded.
     * Processes each modified nodelist file from MinIO storage.
     * @param modifiedObjects list of MinIO object paths that were modified
     */
    @KafkaListener(topics = "download_nodelists_is_finished_topic")
    private void updateNodelist(List<String> modifiedObjects) {
        log.info("Update nodelists is started");
        for (String object : modifiedObjects) {
            Matcher matcher = Pattern.compile(minioPath + "(\\d{4})/(nodelist\\.\\d{3})").matcher(object);

            if (matcher.matches()) {
                return;
            }

            try (InputStream inputStream = minioUtils.getObject(minioBucket, object)) {
                Nodelist nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
                updateNodelist(nodelist, Integer.parseInt(matcher.group(1)), matcher.group(2));
            } catch (Exception e) {
                log.error("Failed to add nodelist to database", e);
            }
        }
        clearRedisCache.clearCache();
        log.info("Update nodelists is finished");
    }

    /**
     * Updates database with entries from a single nodelist file
     * @param nodelist parsed nodelist object
     * @param year year of the nodelist
     * @param name name of the nodelist file
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
