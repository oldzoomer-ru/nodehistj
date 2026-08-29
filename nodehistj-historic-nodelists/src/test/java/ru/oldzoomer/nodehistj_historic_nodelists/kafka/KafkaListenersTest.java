package ru.oldzoomer.nodehistj_historic_nodelists.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.oldzoomer.nodehistj_historic_nodelists.util.NodelistFillToDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaListeners in nodehistj-historic-nodelists module.
 * Tests message processing, acknowledgment, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class KafkaListenersTest {

    @Mock
    private NodelistFillToDatabase nodelistFillToDatabase;

    @Mock
    private Acknowledgment ack;

    @InjectMocks
    private KafkaListeners listener;

    @Test
    void downloadNodelistsIsFinishedListener_withValidMessage_shouldProcessAndAcknowledge() {
        List<String> files = List.of("/path/to/file1.nodelist", "/path/to/file2.nodelist");

        listener.downloadNodelistsIsFinishedListener(files, ack);

        verify(nodelistFillToDatabase).updateNodelist(files);
        verify(ack).acknowledge();
    }

    @Test
    void downloadNodelistsIsFinishedListener_withNullMessage_shouldAcknowledgeWithoutProcessing() {
        listener.downloadNodelistsIsFinishedListener(null, ack);

        verify(nodelistFillToDatabase, never()).updateNodelist(any());
        verify(ack).acknowledge();
    }

    @Test
    void downloadNodelistsIsFinishedListener_withEmptyMessage_shouldAcknowledgeWithoutProcessing() {
        listener.downloadNodelistsIsFinishedListener(List.of(), ack);

        verify(nodelistFillToDatabase, never()).updateNodelist(any());
        verify(ack).acknowledge();
    }

    @Test
    void downloadNodelistsIsFinishedListener_withSingleFile_shouldProcessCorrectly() {
        List<String> files = List.of("/path/to/single.nodelist");

        listener.downloadNodelistsIsFinishedListener(files, ack);

        verify(nodelistFillToDatabase).updateNodelist(files);
        verify(ack).acknowledge();
    }

    @Test
    void downloadNodelistsIsFinishedListener_withDatabaseError_shouldThrowExceptionAndNotAcknowledge() {
        List<String> files = List.of("/path/to/file.nodelist");
        doThrow(new RuntimeException("Database error")).when(nodelistFillToDatabase).updateNodelist(files);

        assertThrows(IllegalStateException.class, () -> {
            listener.downloadNodelistsIsFinishedListener(files, ack);
        });

        verify(ack, never()).acknowledge();
    }

    @Test
    void downloadNodelistsIsFinishedListener_withMultipleFiles_shouldProcessAll() {
        List<String> files = List.of(
                "/path/to/file1.nodelist",
                "/path/to/file2.nodelist",
                "/path/to/file3.nodelist"
        );

        listener.downloadNodelistsIsFinishedListener(files, ack);

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(nodelistFillToDatabase).updateNodelist(captor.capture());

        List<String> capturedFiles = captor.getValue();
        assertEquals(3, capturedFiles.size());
        assertTrue(capturedFiles.contains("/path/to/file1.nodelist"));
        assertTrue(capturedFiles.contains("/path/to/file2.nodelist"));
        assertTrue(capturedFiles.contains("/path/to/file3.nodelist"));
        verify(ack).acknowledge();
    }

    @Test
    void downloadNodelistsIsFinishedListener_withLargeMessage_shouldProcessAllFiles() {
        List<String> files = List.of(
                "/path/to/file1.nodelist", "/path/to/file2.nodelist",
                "/path/to/file3.nodelist", "/path/to/file4.nodelist",
                "/path/to/file5.nodelist", "/path/to/file6.nodelist",
                "/path/to/file7.nodelist", "/path/to/file8.nodelist",
                "/path/to/file9.nodelist", "/path/to/file10.nodelist"
        );

        listener.downloadNodelistsIsFinishedListener(files, ack);

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(nodelistFillToDatabase).updateNodelist(captor.capture());

        assertEquals(10, captor.getValue().size());
        verify(ack).acknowledge();
    }
}
