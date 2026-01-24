package ru.oldzoomer.nodehistj_history_diff.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.oldzoomer.nodehistj_history_diff.util.NodelistDiffProcessor;
import ru.oldzoomer.nodehistj_history_diff.util.NodelistFillToDatabase;

import java.util.List;

/**
 * Component that listens to Kafka topics and processes messages.
 * Handles messages from the download_nodelists_is_finished_topic topic.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class KafkaListeners {
    private final NodelistFillToDatabase nodelistFillToDatabase;
    private final NodelistDiffProcessor nodelistDiffProcessor;

    /**
     * Listens to the download_nodelists_is_finished_topic topic and updates the nodelist in the database.
     *
     * @param message the list of nodelist file paths that were downloaded
     */
    @KafkaListener(topics = "download_nodelists_is_finished_topic", concurrency = "1")
    public void downloadNodelistsIsFinishedListener(List<String> message, Acknowledgment ack) {
        log.debug("Received message from download_nodelists_is_finished_topic with {} files",
                message != null ? message.size() : 0);
        try {
            if (message == null || message.isEmpty()) {
                log.warn("Received empty or null message from Kafka topic");
                ack.acknowledge();
                return;
            }
            nodelistFillToDatabase.updateNodelist(message);
            nodelistDiffProcessor.processNodelistDiffs();
            log.info("Successfully processed {} files from Kafka message", message.size());
            // Acknowledge only after successful processing
            ack.acknowledge();
            log.debug("Acknowledged Kafka message processing");
        } catch (Exception e) {
            log.error("Error processing Kafka message with {} files",
                    message != null ? message.size() : 0, e);
            // Don't acknowledge to let Kafka retry the message
            throw new IllegalStateException("Failed to process Kafka message", e);
        }
    }
}
