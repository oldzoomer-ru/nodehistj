package ru.oldzoomer.nodelistj_download_nodelists.config;

import org.apache.kafka.clients.admin.NewTopic;
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
    NewTopic downloadNodelistsIsFinishedTopic() {
        return TopicBuilder.name("download_nodelists_is_finished_topic")
                .partitions(1)
                .build();
    }
}
