package ru.oldzoomer.nodehistj_history_diff.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.oldzoomer.nodehistj_history_diff.util.NodelistDiffProcessor;
import ru.oldzoomer.nodehistj_history_diff.util.NodelistFillToDatabase;

import java.util.List;

/**
 * Component that listens to Kafka topics and processes messages.
 * Handles messages from the download_nodelists_is_finished_topic topic.
 */
@Component
@Slf4j
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
    public void downloadNodelistsIsFinishedListener(List<String> message) {
        log.debug("Received message from download_nodelists_is_finished_topic");
        try {
            nodelistFillToDatabase.updateNodelist(message);
            nodelistDiffProcessor.processNodelistDiffs();
            log.info("Nodelist update completed successfully");
        } catch (Exception e) {
            log.error("Error updating nodelist", e);
        }
    }
}
