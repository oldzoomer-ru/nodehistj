package ru.gavrilovegor519.nodehistj_historic_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import ru.gavrilovegor519.Nodelist;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntity;
import ru.gavrilovegor519.nodehistj_historic_nodelists.repo.NodelistEntityRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
                    NodelistEntity nodelistEntity = new NodelistEntity();
                    nodelistEntity.setNodelist(nodelist.getNodelistEntries());
                    nodelistEntity.setNodelistYear(Integer.valueOf(matcher.group(1)));
                    nodelistEntity.setNodelistName(matcher.group(2));
                    nodelistEntityRepository.save(nodelistEntity);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to retrieve nodelist from Minio", e);
                }
            }
        }
        log.info("Update nodelists is finished");
    }
}
