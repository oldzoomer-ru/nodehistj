package ru.oldzoomer.nodehistj_newest_nodelists.kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_newest_nodelists.util.NodelistFillToDatabase;

/**
 * Kafka listener component for handling messages from various topics.
 * <p>
 * Responsibilities:
 * - Listening to Kafka topics for new messages
 * - Processing received messages
 * - Delegating message processing to appropriate services
 * <p>
 * This component acts as an entry point for Kafka message processing in the application.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListeners {
    private final NodelistFillToDatabase nodelistFillToDatabase;

    /**
     * Listens to the download_nodelists_is_finished_topic for new messages.
     * <p>
     * When a message is received, it logs the event and delegates the processing
     * to the NodelistFillToDatabase service.
     *
     * @param message List of strings containing information about downloaded files
     */
    @KafkaListener(topics = "download_nodelists_is_finished_topic")
    public void downloadNodelistsIsFinishedListener(List<String> message) {
        log.debug("Received message from download_nodelists_is_finished_topic");
        nodelistFillToDatabase.updateNodelist(message);
    }
}
