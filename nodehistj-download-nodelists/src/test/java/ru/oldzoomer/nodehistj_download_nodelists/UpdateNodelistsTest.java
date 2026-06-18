package ru.oldzoomer.nodehistj_download_nodelists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.oldzoomer.nodehistj.s3.utils.S3Utils;
import ru.oldzoomer.nodehistj_download_nodelists.exception.NodelistUpdateException;
import ru.oldzoomer.nodehistj_download_nodelists.util.FtpClient;
import ru.oldzoomer.nodehistj_download_nodelists.util.UpdateNodelists;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UpdateNodelists}.
 * <p>
 * Tests cover:
 * - Successful nodelist download and upload to S3
 * - Skipping already existing files in S3
 * - Configuration validation
 * - Error handling (IO errors, file download failures)
 */
@ExtendWith(MockitoExtension.class)
class UpdateNodelistsTest {

    @Mock
    private S3Utils s3Utils;

    @Mock
    private FtpClient ftpClient;

    @Mock
    private KafkaTemplate<String, List<String>> kafkaTemplate;

    private UpdateNodelists updateNodelists;

    private static final int CURRENT_YEAR = Year.now().getValue();

    @BeforeEach
    void setUp() throws Exception {
        updateNodelists = new UpdateNodelists(s3Utils, ftpClient, kafkaTemplate);

        // Set configuration values via reflection
        setField(updateNodelists, "ftpPath", "ftp://ftp.example.com/nodelists/");
        setField(updateNodelists, "downloadFromYear", 2020);
        setField(updateNodelists, "bucket", "test-bucket");
    }

    private void setField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // Scenario 0: Successful download and upload
    @Test
    void updateNodelists_shouldDownloadNewFilesAndUploadToS3() throws IOException {
        // Given — simulate only current year has files, previous years return empty
        lenient().when(ftpClient.listFiles(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            if (path.contains(String.valueOf(CURRENT_YEAR))) {
                return new String[]{CURRENT_YEAR + "/nodelist.001", CURRENT_YEAR + "/nodelist.002"};
            }
            return new String[0];
        });

        lenient().when(s3Utils.isObjectExist(anyString(), anyString())).thenReturn(false);

        ByteArrayOutputStream mockStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream mockStream2 = new ByteArrayOutputStream();
        lenient().when(ftpClient.downloadFile(CURRENT_YEAR + "/nodelist.001")).thenReturn(mockStream1);
        lenient().when(ftpClient.downloadFile(CURRENT_YEAR + "/nodelist.002")).thenReturn(mockStream2);

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(s3Utils, times(1)).createBucket("test-bucket");
        verify(ftpClient, times(1)).open();
        verify(ftpClient, times(7)).listFiles(anyString()); // 7 years: 2026, 2025, 2024, 2023, 2022, 2021, 2020
        verify(ftpClient, times(2)).downloadFile(anyString());
        verify(s3Utils, times(2)).putObject(anyString(), anyString(), any(byte[].class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> kafkaCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate, times(1)).send(eq("download_nodelists_is_finished_topic"), kafkaCaptor.capture());
        List<String> sentFiles = kafkaCaptor.getValue();
        assertThat(sentFiles).containsExactlyInAnyOrder(CURRENT_YEAR + "/nodelist.001", CURRENT_YEAR + "/nodelist.002");

        verify(ftpClient, times(1)).close();
    }

    // Scenario 1: Skip existing files
    @Test
    void updateNodelists_shouldSkipExistingFilesInS3() throws IOException {
        // Given
        String existingFile = CURRENT_YEAR + "/nodelist.001";
        String newFile = CURRENT_YEAR + "/nodelist.002";
        String[] filesCurrentYear = {existingFile, newFile};

        lenient().when(ftpClient.listFiles(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            if (path.contains(String.valueOf(CURRENT_YEAR))) {
                return filesCurrentYear;
            }
            return new String[0];
        });

        lenient().when(s3Utils.isObjectExist("test-bucket", existingFile)).thenReturn(true); // already exists
        lenient().when(s3Utils.isObjectExist("test-bucket", newFile)).thenReturn(false); // new

        ByteArrayOutputStream mockStream = new ByteArrayOutputStream();
        lenient().when(ftpClient.downloadFile(newFile)).thenReturn(mockStream);

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(s3Utils, never()).putObject(anyString(), eq(existingFile), any(byte[].class));
        verify(s3Utils, times(1)).putObject(anyString(), eq(newFile), any(byte[].class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> kafkaCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate, times(1)).send(eq("download_nodelists_is_finished_topic"), kafkaCaptor.capture());
        assertThat(kafkaCaptor.getValue()).containsExactly(newFile);
    }

    // Scenario 2: Validate configuration parameters
    @Test
    void updateNodelists_shouldThrowExceptionWhenFtpPathIsEmpty() throws Exception {
        setField(updateNodelists, "ftpPath", "");

        assertThatThrownBy(() -> updateNodelists.updateNodelists())
            .isInstanceOf(NodelistUpdateException.class)
            .hasCauseInstanceOf(IllegalArgumentException.class)
            .hasRootCauseMessage("FTP path cannot be empty or null");

        verify(ftpClient, never()).open();
    }

    @Test
    void updateNodelists_shouldThrowExceptionWhenFtpPathIsNull() throws Exception {
        setField(updateNodelists, "ftpPath", (String) null);

        assertThatThrownBy(() -> updateNodelists.updateNodelists())
            .isInstanceOf(NodelistUpdateException.class)
            .hasCauseInstanceOf(IllegalArgumentException.class)
            .hasRootCauseMessage("FTP path cannot be empty or null");

        verify(ftpClient, never()).open();
    }

    @Test
    void updateNodelists_shouldThrowExceptionWhenBucketIsEmpty() throws Exception {
        setField(updateNodelists, "bucket", "");

        assertThatThrownBy(() -> updateNodelists.updateNodelists())
            .isInstanceOf(NodelistUpdateException.class)
            .hasCauseInstanceOf(IllegalArgumentException.class)
            .hasRootCauseMessage("Minio bucket cannot be empty or null");

        verify(ftpClient, never()).open();
    }

    @Test
    void updateNodelists_shouldThrowExceptionWhenBucketIsNull() throws Exception {
        setField(updateNodelists, "bucket", (String) null);

        assertThatThrownBy(() -> updateNodelists.updateNodelists())
            .isInstanceOf(NodelistUpdateException.class)
            .hasCauseInstanceOf(IllegalArgumentException.class)
            .hasRootCauseMessage("Minio bucket cannot be empty or null");

        verify(ftpClient, never()).open();
    }

    @Test
    void updateNodelists_shouldThrowExceptionWhenDownloadFromYearIsGreaterThanCurrentYear() throws Exception {
        setField(updateNodelists, "downloadFromYear", Year.now().getValue() + 1);

        assertThatThrownBy(() -> updateNodelists.updateNodelists())
            .isInstanceOf(NodelistUpdateException.class)
            .hasCauseInstanceOf(IllegalArgumentException.class)
            .cause()
            .hasMessage("Download from year (" + (Year.now().getValue() + 1) +
                ") cannot be greater than current year (" + Year.now().getValue() + ")");

        verify(ftpClient, times(0)).open();
    }

    @Test
    void updateNodelists_shouldThrowExceptionWhenDownloadFromYearIsLessThan1980() throws Exception {
        setField(updateNodelists, "downloadFromYear", 1979);

        assertThatThrownBy(() -> updateNodelists.updateNodelists())
            .isInstanceOf(NodelistUpdateException.class)
            .hasCauseInstanceOf(IllegalArgumentException.class)
            .cause()
            .hasMessage("Download from year (1979) cannot be less than 1980");

        verify(ftpClient, times(0)).open();
    }

    // Scenario 3: Handle IO errors
    @Test
    void updateNodelists_shouldThrowNodelistUpdateExceptionOnIoError() throws IOException {
        // Given
        lenient().when(ftpClient.listFiles(anyString()))
            .thenThrow(new IOException("Connection reset"));

        // When/Then
        assertThatThrownBy(() -> updateNodelists.updateNodelists())
            .isInstanceOf(NodelistUpdateException.class)
            .hasMessageContaining("IO error");

        verify(ftpClient, times(1)).close();
    }

    @Test
    void updateNodelists_shouldCloseFtpConnectionOnSuccess() throws IOException {
        // Given
        lenient().when(ftpClient.listFiles(anyString())).thenReturn(new String[0]);

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(ftpClient, times(1)).open();
        verify(ftpClient, times(1)).close();
    }

    // Scenario 4: Handle partial download failures
    @Test
    void updateNodelists_shouldContinueProcessingWhenOneFileFails() throws IOException {
        // Given
        String failedFile = CURRENT_YEAR + "/nodelist.002";
        String successFile = CURRENT_YEAR + "/nodelist.001";
        String[] filesCurrentYear = {successFile, failedFile};

        lenient().when(ftpClient.listFiles(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            if (path.contains(String.valueOf(CURRENT_YEAR))) {
                return filesCurrentYear;
            }
            return new String[0];
        });

        lenient().when(s3Utils.isObjectExist(anyString(), anyString())).thenReturn(false);

        ByteArrayOutputStream mockStream = new ByteArrayOutputStream();
        lenient().when(ftpClient.downloadFile(successFile)).thenReturn(mockStream);
        lenient().when(ftpClient.downloadFile(failedFile)).thenThrow(new IOException("Download failed"));

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(s3Utils, times(1)).putObject(anyString(), eq(successFile), any(byte[].class));
        verify(s3Utils, never()).putObject(anyString(), eq(failedFile), any(byte[].class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> kafkaCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate, times(1)).send(eq("download_nodelists_is_finished_topic"), kafkaCaptor.capture());
        assertThat(kafkaCaptor.getValue()).containsExactly(successFile);
    }

    // Edge case: No files found
    @Test
    void updateNodelists_shouldHandleNoFilesFound() throws IOException {
        // Given
        lenient().when(ftpClient.listFiles(anyString())).thenReturn(new String[0]);

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(kafkaTemplate, never()).send(anyString(), anyList());
        verify(ftpClient, times(1)).close();
    }

    // Edge case: normalizeObjectName with leading slash
    @Test
    void updateNodelists_shouldNormalizeObjectNamesWithLeadingSlash() throws IOException {
        // Given
        String normalizedFile = CURRENT_YEAR + "/nodelist.001";
        String[] filesCurrentYear = {"/" + normalizedFile};

        lenient().when(ftpClient.listFiles(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            if (path.contains(String.valueOf(CURRENT_YEAR))) {
                return filesCurrentYear;
            }
            return new String[0];
        });

        lenient().when(s3Utils.isObjectExist(anyString(), anyString())).thenReturn(false);

        ByteArrayOutputStream mockStream = new ByteArrayOutputStream();
        lenient().when(ftpClient.downloadFile("/" + normalizedFile)).thenReturn(mockStream);

        // When
        updateNodelists.updateNodelists();

        // Then
        verify(s3Utils, times(1)).putObject(anyString(), eq(normalizedFile), any(byte[].class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> kafkaCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate, times(1)).send(eq("download_nodelists_is_finished_topic"), kafkaCaptor.capture());
        assertThat(kafkaCaptor.getValue()).containsExactly(normalizedFile);
    }
}
