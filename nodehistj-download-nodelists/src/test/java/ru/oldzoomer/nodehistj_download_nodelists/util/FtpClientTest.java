package ru.oldzoomer.nodehistj_download_nodelists.util;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FtpClient.
 * Uses reflection to inject a mocked Apache Commons FTPClient.
 */
@ExtendWith(MockitoExtension.class)
class FtpClientTest {

    @Mock
    private FTPClient mockApacheFtp;

    private FtpClient ftpClient;

    private static final String TEST_SERVER = "ftp.example.com";
    private static final int TEST_PORT = 21;
    private static final String TEST_USER = "testuser";
    private static final String TEST_PASSWORD = "testpass";

    @BeforeEach
    void setUp() {
        ftpClient = new FtpClient();
        ReflectionTestUtils.setField(ftpClient, "server", TEST_SERVER);
        ReflectionTestUtils.setField(ftpClient, "port", TEST_PORT);
        ReflectionTestUtils.setField(ftpClient, "user", TEST_USER);
        ReflectionTestUtils.setField(ftpClient, "password", TEST_PASSWORD);
        ReflectionTestUtils.setField(ftpClient, "ftp", mockApacheFtp);
    }

    @AfterEach
    void tearDown() {
        if (ftpClient != null) {
            ReflectionTestUtils.setField(ftpClient, "ftp", null);
        }
    }

    @Test
    void listFiles_validPath_shouldReturnFileList() throws Exception {
        String[] expectedFiles = {"file1.txt", "file2.txt"};
        when(mockApacheFtp.listNames("/test/path")).thenReturn(expectedFiles);

        String[] result = ftpClient.listFiles("/test/path");

        assertArrayEquals(expectedFiles, result);
        verify(mockApacheFtp).listNames("/test/path");
    }

    @Test
    void listFiles_nullResult_shouldReturnEmptyArray() throws Exception {
        when(mockApacheFtp.listNames("/empty/path")).thenReturn(null);

        String[] result = ftpClient.listFiles("/empty/path");

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void listFiles_ioException_shouldPropagate() throws Exception {
        when(mockApacheFtp.listNames("/bad/path")).thenThrow(new IOException("Connection lost"));

        assertThrows(IOException.class, () -> ftpClient.listFiles("/bad/path"));
    }

    @Test
    void downloadFile_validSource_shouldReturnContent() throws Exception {
        byte[] expectedContent = "test nodelist content".getBytes();
        when(mockApacheFtp.retrieveFile(eq("/test/file.txt"), any(ByteArrayOutputStream.class)))
                .thenAnswer(invocation -> {
                    ByteArrayOutputStream out = invocation.getArgument(1);
                    out.write(expectedContent);
                    return true;
                });

        ByteArrayOutputStream result = ftpClient.downloadFile("/test/file.txt");

        assertNotNull(result);
        assertArrayEquals(expectedContent, result.toByteArray());
    }

    @Test
    void downloadFile_failedRetrieval_shouldThrowIOException() throws Exception {
        when(mockApacheFtp.retrieveFile(eq("/bad/file.txt"), any(ByteArrayOutputStream.class)))
                .thenReturn(false);

        assertThrows(IOException.class, () -> ftpClient.downloadFile("/bad/file.txt"));
    }

    @Test
    void downloadFile_ioException_shouldPropagate() throws Exception {
        when(mockApacheFtp.retrieveFile(eq("/error/file.txt"), any(ByteArrayOutputStream.class)))
                .thenThrow(new IOException("Download failed"));

        assertThrows(IOException.class, () -> ftpClient.downloadFile("/error/file.txt"));
    }

    @Test
    void close_shouldDisconnectAndNullifyFtp() throws Exception {
        ftpClient.close();

        verify(mockApacheFtp).disconnect();
        Object ftpField = ReflectionTestUtils.getField(ftpClient, "ftp");
        assertNull(ftpField);
    }

    @Test
    void close_disconnectException_shouldPropagate() throws Exception {
        IOException disconnectError = new IOException("Disconnect failed");
        doThrow(disconnectError).when(mockApacheFtp).disconnect();

        assertThrows(IOException.class, () -> ftpClient.close());
        // ftp should still be nullified in finally block
        Object ftpField = ReflectionTestUtils.getField(ftpClient, "ftp");
        assertNull(ftpField);
    }

    @Test
    void close_nullFtp_shouldNotThrow() throws Exception {
        ReflectionTestUtils.setField(ftpClient, "ftp", null);

        ftpClient.close();
        // Should complete without exception
    }

}
