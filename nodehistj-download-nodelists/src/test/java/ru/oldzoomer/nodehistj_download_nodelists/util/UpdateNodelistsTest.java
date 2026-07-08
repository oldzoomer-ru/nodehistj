package ru.oldzoomer.nodehistj_download_nodelists.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.oldzoomer.nodehistj.s3.utils.S3Utils;
import ru.oldzoomer.nodehistj_download_nodelists.exception.NodelistUpdateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateNodelistsTest {

    @Mock
    private S3Utils s3Utils;

    @Mock
    private FtpClient ftpClient;

    @Mock
    private KafkaTemplate<String, List<String>> kafkaTemplate;

    private UpdateNodelists updateNodelists;

    private static final String TEST_FTP_PATH = "/nodelists/";
    private static final String TEST_BUCKET = "test-bucket";
    private static final int TEST_DOWNLOAD_FROM_YEAR = 2020;
    private static final int CURRENT_YEAR = java.time.Year.now().getValue();

    @BeforeEach
    void setUp() throws Exception {
        reset(s3Utils, ftpClient, kafkaTemplate);
        // Создаём экземпляр вручную, т.к. @InjectMocks не работает с @Value полями
        updateNodelists = new UpdateNodelists(s3Utils, ftpClient, kafkaTemplate);
        setField(updateNodelists, "ftpPath", TEST_FTP_PATH);
        setField(updateNodelists, "bucket", TEST_BUCKET);
        setField(updateNodelists, "downloadFromYear", TEST_DOWNLOAD_FROM_YEAR);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateNodelists_WithNewFiles_DownloadsAndSendsToKafka() throws IOException {
        // Given
        String[] filesCurrentYear = {CURRENT_YEAR + "/nodelist.001", CURRENT_YEAR + "/nodelist.002"};
        String[] filesPrevYear = {(CURRENT_YEAR - 1) + "/nodelist.001"};

        when(s3Utils.isObjectExist(eq(TEST_BUCKET), anyString())).thenReturn(false);
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + CURRENT_YEAR))).thenReturn(filesCurrentYear);
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + (CURRENT_YEAR - 1)))).thenReturn(filesPrevYear);

        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();

        when(ftpClient.downloadFile(CURRENT_YEAR + "/nodelist.001")).thenReturn(stream1);
        when(ftpClient.downloadFile(CURRENT_YEAR + "/nodelist.002")).thenReturn(stream2);
        when(ftpClient.downloadFile((CURRENT_YEAR - 1) + "/nodelist.001")).thenReturn(stream3);

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(s3Utils).createBucket(TEST_BUCKET);
        verify(ftpClient).open();
        verify(ftpClient).listFiles(TEST_FTP_PATH + 2024);
        verify(ftpClient).listFiles(TEST_FTP_PATH + 2023);
        verify(ftpClient).downloadFile(CURRENT_YEAR + "/nodelist.001");
        verify(ftpClient).downloadFile(CURRENT_YEAR + "/nodelist.002");
        verify(ftpClient).downloadFile((CURRENT_YEAR - 1) + "/nodelist.001");
        verify(s3Utils).putObject(eq(TEST_BUCKET), eq(CURRENT_YEAR + "/nodelist.001"), any(byte[].class));
        verify(s3Utils).putObject(eq(TEST_BUCKET), eq(CURRENT_YEAR + "/nodelist.002"), any(byte[].class));
        verify(s3Utils).putObject(eq(TEST_BUCKET), eq((CURRENT_YEAR - 1) + "/nodelist.001"), any(byte[].class));

        ArgumentCaptor<List<String>> filesCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate).send(eq("download_nodelists_is_finished_topic"), filesCaptor.capture());
        List<String> sentFiles = filesCaptor.getValue();
        assertEquals(3, sentFiles.size());
        assertTrue(sentFiles.contains(CURRENT_YEAR + "/nodelist.001"));
        assertTrue(sentFiles.contains(CURRENT_YEAR + "/nodelist.002"));
        assertTrue(sentFiles.contains((CURRENT_YEAR - 1) + "/nodelist.001"));
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_WithNoNewFiles_DoesNotUploadButSkipsKafka() throws IOException {
        // Given
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), anyString())).thenReturn(true);
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + CURRENT_YEAR))).thenReturn(new String[]{CURRENT_YEAR + "/nodelist.001"});

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(ftpClient).open();
        verify(ftpClient).listFiles(TEST_FTP_PATH + 2024);
        verify(ftpClient, never()).downloadFile(anyString());
        verify(s3Utils, never()).putObject(anyString(), anyString(), any(byte[].class));
        verify(kafkaTemplate, never()).send(anyString(), anyList());
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_WithIOException_ThrowsNodelistUpdateException() throws IOException {
        // Given
        when(ftpClient.listFiles(anyString())).thenThrow(new IOException("Connection lost"));

        // When & Then
        NodelistUpdateException exception = assertThrows(NodelistUpdateException.class, () ->
                updateNodelists.updateNodelists());
        assertTrue(exception.getMessage().contains("IO error"));
        assertNotNull(exception.getCause());
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_WithSingleFileProcessing_FailsGracefullyAndContinues() throws IOException {
        // Given
        String[] filesCurrentYear = {CURRENT_YEAR + "/nodelist.001", CURRENT_YEAR + "/nodelist.002"};
        when(s3Utils.isObjectExist(eq(TEST_BUCKET), anyString())).thenReturn(false);
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + CURRENT_YEAR))).thenReturn(filesCurrentYear);

        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        when(ftpClient.downloadFile(CURRENT_YEAR + "/nodelist.001")).thenReturn(stream1);
        when(ftpClient.downloadFile(CURRENT_YEAR + "/nodelist.002")).thenThrow(new IOException("Download failed"));

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(s3Utils).putObject(eq(TEST_BUCKET), eq(CURRENT_YEAR + "/nodelist.001"), any(byte[].class));
        verify(s3Utils, never()).putObject(eq(TEST_BUCKET), eq(CURRENT_YEAR + "/nodelist.002"), any(byte[].class));
        verify(kafkaTemplate).send(eq("download_nodelists_is_finished_topic"), argThat(files ->
        {
            assert files != null;
            return files.size() == 1 && files.contains(CURRENT_YEAR + "/nodelist.001");
        }));
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_WithNoFilesForYear_ReturnsZeroAndSkipsKafka() throws IOException {
        // Given
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + CURRENT_YEAR))).thenReturn(new String[0]);
        when(ftpClient.listFiles(eq(TEST_FTP_PATH + (CURRENT_YEAR - 1)))).thenReturn(new String[0]);

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(ftpClient).listFiles(TEST_FTP_PATH + CURRENT_YEAR);
        verify(ftpClient).listFiles(TEST_FTP_PATH + (CURRENT_YEAR - 1));
        verify(kafkaTemplate, never()).send(anyString(), anyList());
        verify(ftpClient).close();
    }

    @Test
    void updateNodelists_WithIOExceptionOnClose_LogsErrorButDoesNotThrow() throws IOException {
        // Given
        when(ftpClient.listFiles(anyString())).thenReturn(new String[0]);
        doThrow(new IOException("Close failed")).when(ftpClient).close();

        // When & Then (should not throw, just log)
        assertDoesNotThrow(() -> updateNodelists.updateNodelists());
        verify(ftpClient).close();
    }
}
