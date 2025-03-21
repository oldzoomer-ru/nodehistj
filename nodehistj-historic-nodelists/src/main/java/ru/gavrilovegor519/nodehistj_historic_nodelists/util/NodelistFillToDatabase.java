package ru.gavrilovegor519.nodehistj_historic_nodelists.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodeEntry;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntry;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodeEntryRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodelistEntryRepository;
import ru.gavrilovegor519.nodelistj.Nodelist;

@RequiredArgsConstructor
@Component
@Log4j2
public class NodelistFillToDatabase {
    private final MinioUtils minioUtils;
    private final NodeEntryRepository nodeEntryRepository;
    private final NodelistEntryRepository nodelistEntryRepository;
    private final ClearRedisCache clearRedisCache;

    @Value("${minio.path}")
    private String minioPath;

    @Value("${minio.bucket}")
    private String minioBucket;

    @KafkaListener(topics = "download_nodelists_is_finished_topic",
            topicPartitions = @TopicPartition(topic = "download_nodelists_is_finished_topic",
                    partitionOffsets = {
                            @PartitionOffset(partition = "0", initialOffset = "0")
                    }))
    private void updateNodelist(List<String> modifiedObjectsDto) {
        log.info("Update nodelists is started");
        for (String object : modifiedObjectsDto) {
            Matcher matcher = Pattern.compile(minioPath + "(\\d{4})/(nodelist\\.\\d{3})").matcher(object);
            if (matcher.matches()) {
                try (InputStream inputStream = minioUtils.getObject("nodehist", object)) {
                    Nodelist nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
                    updateNodelist(nodelist, Integer.parseInt(matcher.group(1)), matcher.group(2));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to retrieve nodelist from Minio", e);
                }
            }
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

            for (ru.gavrilovegor519.nodelistj.entries.NodelistEntry nodeListEntry : nodelist.getNodelist()) {
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

                nodeEntryRepository.save(nodeEntryNew);
            }
            log.info("Update nodelist from {} year and name \"{}\" is finished", year, name);
        }
    }
}
