package ru.oldzoomer.nodehistj_download_nodelists.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
    /**
     * Topic for signaling when nodelist downloads are complete
     * with partitions for parallel processing
     */
    @Bean
    NewTopic downloadNodelistsIsFinishedTopic(
            @Value("${spring.kafka.topic.download-nodelists-is-finished-topic.partitions}") int partitions,
            @Value("${spring.kafka.topic.download-nodelists-is-finished-topic.replicas}") int replicas) {
        return TopicBuilder.name("download_nodelists_is_finished_topic")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
