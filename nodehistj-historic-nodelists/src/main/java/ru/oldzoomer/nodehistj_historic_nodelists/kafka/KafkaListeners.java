package ru.oldzoomer.nodehistj_historic_nodelists.kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.oldzoomer.nodehistj_historic_nodelists.util.NodelistFillToDatabase;

/**
 * Component that listens to Kafka topics and processes messages.
 * Handles messages from the download_nodelists_is_finished_topic topic.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class KafkaListeners {
    private final NodelistFillToDatabase nodelistFillToDatabase;

    /**
     * Listens to the download_nodelists_is_finished_topic topic and updates the nodelist in the database.
     *
     * @param message the list of nodelist file paths that were downloaded
     */
    @KafkaListener(topics = "download_nodelists_is_finished_topic", concurrency = "1")
    public void downloadNodelistsIsFinishedListener(List<String> message, Acknowledgment ack) {
        log.debug("Received message from download_nodelists_is_finished_topic");
        try {
            nodelistFillToDatabase.updateNodelist(message);
        } catch (Exception e) {
            log.error(e);
        } finally {
            ack.acknowledge();
        }
    }
}
