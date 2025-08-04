package ru.oldzoomer.nodehistj_history_diff.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.oldzoomer.nodehistj_history_diff.util.NodelistDiffProcessor;
import ru.oldzoomer.nodehistj_history_diff.util.NodelistFillToDatabase;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class KafkaListeners {
    private final NodelistFillToDatabase nodelistFillToDatabase;
    private final NodelistDiffProcessor nodelistDiffProcessor;

    @KafkaListener(topics = "download_nodelists_is_finished_topic")
    public void downloadNodelistsIsFinishedListener(List<String> message) {
        log.debug("Received message from download_nodelists_is_finished_topic");
        nodelistFillToDatabase.updateNodelist(message);
        nodelistDiffProcessor.processNodelistDiffs(message);
    }
}
