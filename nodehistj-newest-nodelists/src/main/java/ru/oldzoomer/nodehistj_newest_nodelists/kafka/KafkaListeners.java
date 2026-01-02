package ru.oldzoomer.nodehistj_newest_nodelists.kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.oldzoomer.nodehistj_newest_nodelists.util.NodelistFillToDatabase;

/**
 * Component that listens to Kafka topics and processes messages.
 * Handles messages from the download_nodelists_is_finished_topic topic.
 */
@Component
@Slf4j
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
        nodelistFillToDatabase.updateNodelist(message);
        ack.acknowledge();
    }
}
