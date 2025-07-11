package ru.oldzoomer.nodehistj_newest_nodelists.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
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
import ru.oldzoomer.common.utils.MinioUtils;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodeEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodeEntryRepository;
import ru.oldzoomer.nodehistj_newest_nodelists.repo.NodelistEntryRepository;
import ru.oldzoomer.nodelistj.Nodelist;

@RequiredArgsConstructor
@Component
@Log4j2
@Profile("!test")
public class NodelistFillToDatabase {
    private final MinioUtils minioUtils;
    private final NodeEntryRepository nodeEntryRepository;
    private final NodelistEntryRepository nodelistEntryRepository;
    private final ClearRedisCache clearRedisCache;

    @Value("${minio.path}")
    private String minioPath;

    @Value("${minio.bucket}")
    private String minioBucket;

    @NotNull
    private static NodeEntry getNodeEntry(
            ru.oldzoomer.nodelistj.entries.NodelistEntry nodeListEntry,
            NodelistEntry nodelistEntryNew) {
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

    @KafkaListener(topics = "download_nodelists_is_finished_topic")
    private void updateNodelist(List<String> modifiedObjects) {
        log.info("Update nodelists is started");

        modifiedObjects.sort(Comparator.reverseOrder());
        if (modifiedObjects.isEmpty()) {
            return;
        }
        String objectName = modifiedObjects.getFirst();

        Matcher matcher = Pattern.compile(minioPath + "(\\d{4})/(nodelist\\.\\d{3})")
                .matcher(objectName);
        if (!matcher.matches()) {
            return;
        }

        String year = matcher.group(1);
        if (Integer.parseInt(year) < LocalDate.now().getYear()) {
            return;
        }
        String name = matcher.group(2);

        try (InputStream inputStream = minioUtils.getObject(minioBucket, objectName)) {
            Nodelist nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
            updateNodelist(nodelist, Integer.parseInt(year), name);
        } catch (Exception e) {
            log.error("Failed to add nodelist to database", e);
        }

        clearRedisCache.clearAllCache();
        log.info("Update nodelists is finished");
    }

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
