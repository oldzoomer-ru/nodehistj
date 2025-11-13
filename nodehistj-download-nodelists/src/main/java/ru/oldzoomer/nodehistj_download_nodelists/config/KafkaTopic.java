package ru.oldzoomer.nodehistj_download_nodelists.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration class for Kafka topics.
 * <p>
 * Responsible for:
 * - Creating Kafka topics
 * - Configuring topic properties
 * - Setting up topic partitions and replicas
 */
@Configuration
public class KafkaTopic {
    /**
     * Creates a Kafka topic for signaling when nodelist downloads are complete.
     *
     * @param partitions Number of partitions for the topic
     * @param replicas Number of replicas for the topic
     * @return NewTopic instance configured for nodelist download completion notifications
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
