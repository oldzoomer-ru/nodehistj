package ru.gavrilovegor519.nodehistj.config;

import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import ru.gavrilovegor519.Nodelist;
import ru.gavrilovegor519.nodehistj.entity.NodelistEntity;
import ru.gavrilovegor519.nodehistj.repo.NodelistEntityRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Year;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class NodelistConfig {
    private final MinioClient minioClient;
    private final NodelistEntityRepository nodelistEntityRepository;

    @Value("${minio.path}")
    private String minioPath;

    @KafkaListener(topics = "download_nodelists_is_finished_topic", groupId = "nodelist_group")
    private void updateNodelist() {
        Iterable<Result<Item>> nodehist = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket("nodehist").build());

        for (Result<Item> result : nodehist) {
            try {
                Item item = result.get();
                if (!item.isDir()) {
                    Iterable<Result<Item>> nodehist1 = minioClient.listObjects(ListObjectsArgs.builder()
                            .bucket("nodehist").prefix(item.objectName()).build());
                    for (Result<Item> result1 : nodehist1) {
                        Item item1 = result1.get();
                        if (!item1.isDir()) {
                            String path = minioPath + "/" + item1.objectName();
                            if (item1.objectName().matches("nodelist\\.\\d{3}") &&
                                    nodelistEntityRepository.existsByNodelistYearAndNodelistName(
                                    Year.of(Integer.parseInt(item.objectName())), item1.objectName())) {
                                try (InputStream inputStream = minioClient.getObject(GetObjectArgs
                                        .builder().bucket("nodehist").object(path).build())) {
                                    Nodelist nodelist = new Nodelist(new ByteArrayInputStream(inputStream.readAllBytes()));
                                    NodelistEntity nodelistEntity = new NodelistEntity();
                                    nodelistEntity.setNodelist(nodelist);
                                    nodelistEntity.setNodelistName(item1.objectName());
                                    nodelistEntity.setNodelistYear(Year.of(Integer.parseInt(item.objectName())));
                                    nodelistEntityRepository.save(nodelistEntity);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to retrieve nodelist from Minio", e);
            }
        }
    }
}
