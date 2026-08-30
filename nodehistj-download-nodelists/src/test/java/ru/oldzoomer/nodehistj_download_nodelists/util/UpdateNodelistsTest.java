package ru.oldzoomer.nodehistj_download_nodelists.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ru.oldzoomer.nodehistj.s3.utils.S3Utils;
import ru.oldzoomer.nodehistj_download_nodelists.exception.NodelistUpdateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateNodelists.
 * Uses Mockito to mock S3, FTP, and Kafka dependencies.
 */
@ExtendWith(MockitoExtension.class)
class UpdateNodelistsTest {

    @Mock
    private S3Utils s3Utils;

    @Mock
    private FtpClient ftpClient;

    @Mock
    private KafkaTemplate<String, List<String>> kafkaTemplate;

    @InjectMocks
    private UpdateNodelists updateNodelists;

    private static final String TEST_FTP_PATH = "/pub/fidonet/";
    private static final String TEST_BUCKET = "test-bucket";
    private static final int TEST_DOWNLOAD_FROM_YEAR = 2023;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(updateNodelists, "ftpPath", TEST_FTP_PATH);
        ReflectionTestUtils.setField(updateNodelists, "bucket", TEST_BUCKET);
        ReflectionTestUtils.setField(updateNodelists, "downloadFromYear", TEST_DOWNLOAD_FROM_YEAR);
    }

    @Test
    void updateNodelists_noNewFiles_shouldSendEmptyKafkaMessage() throws Exception {
        int currentYear = Year.now().getValue();
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear))).thenReturn(new String[0]);

        updateNodelists.updateNodelists();

        verify(s3Utils).createBucket(TEST_BUCKET);
        verify(ftpClient).open();
        verify(ftpClient).close();
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void updateNodelists_newFilesFound_shouldDownloadAndSendToKafka() throws Exception {
        int currentYear = Year.now().getValue();
        String filePath = "/pub/fidonet/" + currentYear + "/nodelist.001";
        String normalizedName = "pub/fidonet/" + currentYear + "/nodelist.001";

        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[]{filePath});
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedName)))
                .thenReturn(false);

        ByteArrayOutputStream fileContent = new ByteArrayOutputStream();
        fileContent.write("dummy nodelist content".getBytes());
        when(ftpClient.downloadFile(filePath)).thenReturn(fileContent);

        updateNodelists.updateNodelists();

        verify(s3Utils).putObject(eq(TEST_BUCKET), eq(normalizedName), any(byte[].class));
        @SuppressWarnings("unchecked") ArgumentCaptor<List<String>> kafkaCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate).send(eq("download_nodelists_is_finished_topic"), kafkaCaptor.capture());
        List<String> sentFiles = kafkaCaptor.getValue();
        assertEquals(1, sentFiles.size());
        assertEquals(normalizedName, sentFiles.getFirst());
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_fileAlreadyExists_shouldSkip() throws Exception {
        int currentYear = Year.now().getValue();
        String filePath = "/pub/fidonet/" + currentYear + "/nodelist.001";
        String normalizedName = "pub/fidonet/" + currentYear + "/nodelist.001";

        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[]{filePath});
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedName)))
                .thenReturn(true);

        updateNodelists.updateNodelists();

        verify(s3Utils, never()).putObject(anyString(), anyString(), any(byte[].class));
        verify(ftpClient, never()).downloadFile(anyString());
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void updateNodelists_multipleYears_shouldProcessEach() throws Exception {
        int currentYear = Year.now().getValue();
        String filePathCurrent = "/pub/fidonet/" + currentYear + "/nodelist.001";
        String filePathPrev = "/pub/fidonet/" + (currentYear - 1) + "/nodelist.001";
        String normalizedCurrent = "pub/fidonet/" + currentYear + "/nodelist.001";
        String normalizedPrev = "pub/fidonet/" + (currentYear - 1) + "/nodelist.001";

        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[]{filePathCurrent});
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + (currentYear - 1))))
                .thenReturn(new String[]{filePathPrev});
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedCurrent)))
                .thenReturn(false);
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedPrev)))
                .thenReturn(false);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        content.write("content".getBytes());
        when(ftpClient.downloadFile(anyString())).thenReturn(content);

        updateNodelists.updateNodelists();

        verify(s3Utils, times(2)).putObject(anyString(), anyString(), any(byte[].class));
        @SuppressWarnings("unchecked") ArgumentCaptor<List<String>> kafkaCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate).send(eq("download_nodelists_is_finished_topic"), kafkaCaptor.capture());
        assertEquals(2, kafkaCaptor.getValue().size());
    }

    @Test
    void updateNodelists_noFilesForYear_shouldSkipYear() throws Exception {
        int currentYear = Year.now().getValue();
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[0]);

        updateNodelists.updateNodelists();

        verify(ftpClient, never()).downloadFile(anyString());
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void updateNodelists_nullFilesForYear_shouldSkipYear() throws Exception {
        int currentYear = Year.now().getValue();
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(null);

        updateNodelists.updateNodelists();

        verify(ftpClient, never()).downloadFile(anyString());
    }

    @Test
    void updateNodelists_ftpOpenThrowsIoException_shouldThrowNodelistUpdateException() throws Exception {
        IOException ioException = new IOException("Connection refused");
        org.mockito.Mockito.doThrow(ioException).when(ftpClient).open();

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_listFilesThrowsIoException_shouldThrowNodelistUpdateException() throws Exception {
        int currentYear = Year.now().getValue();
        IOException ioException = new IOException("FTP read error");
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenThrow(ioException);

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_downloadFailsForOneFile_shouldContinueWithOtherFiles() throws Exception {
        int currentYear = Year.now().getValue();
        String goodFile = "/pub/fidonet/" + currentYear + "/nodelist.001";
        String badFile = "/pub/fidonet/" + currentYear + "/nodelist.002";
        String normalizedGood = "pub/fidonet/" + currentYear + "/nodelist.001";
        String normalizedBad = "pub/fidonet/" + currentYear + "/nodelist.002";

        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[]{goodFile, badFile});
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedGood)))
                .thenReturn(false);
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedBad)))
                .thenReturn(false);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        content.write("good content".getBytes());
        when(ftpClient.downloadFile(goodFile)).thenReturn(content);
        when(ftpClient.downloadFile(badFile)).thenThrow(new IOException("Download error"));

        updateNodelists.updateNodelists();

        // Only the good file should be uploaded
        verify(s3Utils).putObject(eq(TEST_BUCKET), eq(normalizedGood), any(byte[].class));
        verify(s3Utils, never()).putObject(eq(TEST_BUCKET), eq(normalizedBad), any(byte[].class));

        // Kafka should only receive the good file
        @SuppressWarnings("unchecked") ArgumentCaptor<List<String>> kafkaCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate).send(eq("download_nodelists_is_finished_topic"), kafkaCaptor.capture());
        assertEquals(1, kafkaCaptor.getValue().size());
        assertEquals(normalizedGood, kafkaCaptor.getValue().getFirst());
    }

    @Test
    void updateNodelists_filesNotMatchingPattern_shouldBeFilteredOut() throws Exception {
        int currentYear = Year.now().getValue();
        String matchingFile = "/pub/fidonet/" + currentYear + "/nodelist.001";
        String nonMatchingFile = "/pub/fidonet/" + currentYear + "/README.txt";
        String normalizedGood = "pub/fidonet/" + currentYear + "/nodelist.001";

        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[]{matchingFile, nonMatchingFile});
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedGood)))
                .thenReturn(false);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        content.write("content".getBytes());
        when(ftpClient.downloadFile(matchingFile)).thenReturn(content);

        updateNodelists.updateNodelists();

        // Only the matching file should be downloaded and uploaded
        verify(ftpClient).downloadFile(matchingFile);
        verify(ftpClient, never()).downloadFile(nonMatchingFile);
        verify(s3Utils, times(1)).putObject(anyString(), anyString(), any(byte[].class));
    }

    @Test
    void updateNodelists_allDownloadsFail_shouldStillNotThrow() throws Exception {
        int currentYear = Year.now().getValue();
        String file = "/pub/fidonet/" + currentYear + "/nodelist.001";
        String normalizedFile = "pub/fidonet/" + currentYear + "/nodelist.001";

        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[]{file});
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(normalizedFile)))
                .thenReturn(false);
        when(ftpClient.downloadFile(file)).thenThrow(new IOException("Download error"));

        // Should not throw - errors are caught per-file
        updateNodelists.updateNodelists();

        verify(s3Utils, never()).putObject(anyString(), anyString(), any(byte[].class));
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void validateInputs_nullFtpPath_shouldThrowIllegalArgumentException() {
        ReflectionTestUtils.setField(updateNodelists, "ftpPath", null);

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
    }

    @Test
    void validateInputs_emptyFtpPath_shouldThrowIllegalArgumentException() {
        ReflectionTestUtils.setField(updateNodelists, "ftpPath", "");

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
    }

    @Test
    void validateInputs_nullBucket_shouldThrowIllegalArgumentException() {
        ReflectionTestUtils.setField(updateNodelists, "bucket", null);

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
    }

    @Test
    void validateInputs_emptyBucket_shouldThrowIllegalArgumentException() {
        ReflectionTestUtils.setField(updateNodelists, "bucket", "");

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
    }

    @Test
    void validateInputs_downloadFromYearGreaterThanCurrent_shouldThrowIllegalArgumentException() {
        ReflectionTestUtils.setField(updateNodelists, "downloadFromYear", Year.now().getValue() + 10);

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
    }

    @Test
    void validateInputs_downloadFromYearBefore1980_shouldThrowIllegalArgumentException() {
        ReflectionTestUtils.setField(updateNodelists, "downloadFromYear", 1979);

        assertThrows(NodelistUpdateException.class, () -> updateNodelists.updateNodelists());
    }

    @Test
    void normalizeObjectName_leadingSlash_shouldBeRemoved() throws Exception {
        int currentYear = Year.now().getValue();
        String filePath = "/pub/fidonet/" + currentYear + "/nodelist.001";
        String expectedNormalized = "pub/fidonet/" + currentYear + "/nodelist.001";

        when(ftpClient.listFiles(eq(TEST_FTP_PATH + currentYear)))
                .thenReturn(new String[]{filePath});
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), eq(expectedNormalized)))
                .thenReturn(false);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        content.write("content".getBytes());
        when(ftpClient.downloadFile(filePath)).thenReturn(content);

        updateNodelists.updateNodelists();

        verify(s3Utils).putObject(eq(TEST_BUCKET), eq(expectedNormalized), any(byte[].class));
    }
}