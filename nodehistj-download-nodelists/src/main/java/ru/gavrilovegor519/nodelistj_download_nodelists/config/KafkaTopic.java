package ru.gavrilovegor519.nodelistj_download_nodelists.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
    @Bean
    public NewTopic downloadNodelistsIsFinishedTopic() {
        return TopicBuilder.name("download_nodelists_is_finished_topic")
                .partitions(2)
                .build();
    }
}
