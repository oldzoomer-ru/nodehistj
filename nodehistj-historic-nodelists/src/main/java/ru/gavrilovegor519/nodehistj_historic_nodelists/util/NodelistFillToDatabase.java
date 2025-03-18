package ru.gavrilovegor519.nodehistj_historic_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntity;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodelistEntityRepository;
import ru.gavrilovegor519.nodelistj.Nodelist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
@Log4j2
public class NodelistFillToDatabase {
    private final MinioUtils minioUtils;
    private final NodelistEntityRepository nodelistEntityRepository;

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
                    nodelist.getNodelist().forEach(nodelistEntry -> {
                        if (!nodelistEntityRepository.isExist(
                                Integer.valueOf(matcher.group(1)),
                                matcher.group(2),
                                nodelistEntry.zone(),
                                nodelistEntry.network(),
                                nodelistEntry.node())) {
                            nodelistEntityRepository.save(NodelistEntity.builder()
                                    .nodelistYear(Integer.valueOf(matcher.group(1)))
                                    .nodelistName(matcher.group(2))
                                    .zone(nodelistEntry.zone())
                                    .network(nodelistEntry.network())
                                    .node(nodelistEntry.node())
                                    .nodeName(nodelistEntry.nodeName())
                                    .location(nodelistEntry.location())
                                    .sysOpName(nodelistEntry.sysOpName())
                                    .phone(nodelistEntry.phone())
                                    .baudRate(nodelistEntry.baudRate())
                                    .flags(Arrays.asList(nodelistEntry.flags()))
                                    .build());
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException("Failed to retrieve nodelist from Minio", e);
                }
            }
        }
        log.info("Update nodelists is finished");
    }
}
