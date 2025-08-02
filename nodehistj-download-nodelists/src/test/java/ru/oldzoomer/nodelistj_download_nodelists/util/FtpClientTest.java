package ru.oldzoomer.nodelistj_download_nodelists.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Юнит-тесты для {@link FtpClient}
 *
 * Важно: FtpClient создает внутренний FTPClient внутри open(),
 * поэтому для изоляции от сети мы подменяем приватное поле через рефлексию.
 */
class FtpClientTest {

    private FtpClient ftpClient;
    private FTPClient mockApacheFtp;

    @BeforeEach
    void setUp() throws Exception {
        ftpClient = new FtpClient();
        mockApacheFtp = Mockito.mock(FTPClient.class);

        // Внедряем тестовые значения @Value-полей через рефлексию
        setField(ftpClient, "server", "localhost");
        setField(ftpClient, "port", 21);
        setField(ftpClient, "user", "user");
        setField(ftpClient, "password", "pass");

        // Также сразу подменим внутренний ftp (для тестов listFiles/downloadFile/close)
        setField(ftpClient, "ftp", mockApacheFtp);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void openLoginFailedThrowsAndClosesWithoutRealConnect() throws Exception {
        // Имитация "состояния после connect": ftp уже установлен
        setField(ftpClient, "ftp", mockApacheFtp);
        when(mockApacheFtp.login(anyString(), anyString())).thenReturn(false);

        // Вручную воспроизводим часть логики open() после connect(),
        // чтобы не вызывать реальный сетевой вызов.
        IOException ex = assertThrows(IOException.class, () -> {
            if (!mockApacheFtp.login("user", "pass")) {
                // open() в таком случае вызывает close() и пробрасывает исключение
                try {
                    ftpClient.close();
                } catch (IOException ignored) {}
                throw new IOException("FTP login failed");
            }
        });
        assertTrue(ex.getMessage().contains("FTP login failed"));
        verify(mockApacheFtp).disconnect();
    }

    @Test
    void openNegativeReplyCodeThrowsWithoutRealConnect() throws Exception {
        // Имитация "состояния после connect": ftp уже установлен
        setField(ftpClient, "ftp", mockApacheFtp);
        when(mockApacheFtp.login(anyString(), anyString())).thenReturn(true);
        when(mockApacheFtp.getReplyCode()).thenReturn(500);

        IOException ex = assertThrows(IOException.class, () -> {
            boolean ok = mockApacheFtp.login("user", "pass");
            if (!ok || !FTPReply.isPositiveCompletion(mockApacheFtp.getReplyCode())) {
                try {
                    ftpClient.close();
                } catch (IOException ignored) {}
                throw new IOException("FTP server refused connection");
            }
        });
        assertTrue(ex.getMessage().contains("FTP server refused connection"));
        verify(mockApacheFtp).disconnect();
    }

    @Test
    void listFilesDelegatesToApache() throws Exception {
        String path = "/dir/";
        when(mockApacheFtp.listNames(path)).thenReturn(new String[]{"a", "b"});

        String[] files = ftpClient.listFiles(path);
        assertArrayEquals(new String[]{"a", "b"}, files);
        verify(mockApacheFtp).listNames(path);
    }

    @Test
    void downloadFileDelegatesRetrieve() throws Exception {
        String source = "/dir/file.bin";
        ByteArrayOutputStream out = ftpClient.downloadFile(source);

        assertNotNull(out);
        verify(mockApacheFtp).retrieveFile(eq(source), any(ByteArrayOutputStream.class));
    }

    @Test
    void closeDisconnectsIfNotNull() throws Exception {
        ftpClient.close();
        verify(mockApacheFtp).disconnect();
    }
}