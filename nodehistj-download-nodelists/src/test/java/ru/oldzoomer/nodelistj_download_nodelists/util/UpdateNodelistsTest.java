package ru.oldzoomer.nodelistj_download_nodelists.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import ru.oldzoomer.minio.MinioUtils;
import ru.oldzoomer.nodelistj_download_nodelists.exception.NodelistUpdateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для {@link UpdateNodelists}
 *
 * Используем чистый Mockito без поднятия Spring-контекста.
 * Значения @Value-инжектов проставляются через рефлексию.
 */
class UpdateNodelistsTest {

    private MinioUtils minioUtils;
    private FtpClient ftpClient;
    private KafkaTemplate<String, List<String>> kafkaTemplate;

    private UpdateNodelists service;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        minioUtils = mock(MinioUtils.class);
        ftpClient = mock(FtpClient.class);
        kafkaTemplate = mock(KafkaTemplate.class);

        service = new UpdateNodelists(minioUtils, ftpClient, kafkaTemplate);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void updateNodelistsHappyPathProcessesFilesAndSendsKafka() throws Exception {
        int currentYear = Year.now().getValue();

        setField(service, "ftpPath", "/nodehist/");
        setField(service, "downloadFromYear", currentYear);
        setField(service, "bucket", "nodehist");

        // Список на FTP
        String yearPath = "/nodehist/" + currentYear + "/";
        String valid1 = yearPath + "nodelist.001";
        String valid2 = yearPath + "nodelist.123";
        String invalid = yearPath + "something.txt";
        when(ftpClient.listFiles(yearPath)).thenReturn(new String[]{valid1, invalid, valid2});

        // В MinIO еще нет объектов
        when(minioUtils.isObjectExist("nodehist", valid1)).thenReturn(false);
        when(minioUtils.isObjectExist("nodehist", valid2)).thenReturn(false);

        // Скачивание
        when(ftpClient.downloadFile(valid1)).thenReturn(new ByteArrayOutputStream());
        when(ftpClient.downloadFile(valid2)).thenReturn(new ByteArrayOutputStream());

        // Выполнение
        service.updateNodelists();

        // Проверки
        verify(minioUtils).createBucket("nodehist");
        verify(ftpClient).open();
        verify(ftpClient).listFiles(yearPath);

        // putObject вызывается для двух новых файлов (без лидирующего слеша)
        verify(minioUtils).putObject(eq("nodehist"), eq(valid1.substring(1)), any(ByteArrayOutputStream.class));
        verify(minioUtils).putObject(eq("nodehist"), eq(valid2.substring(1)), any(ByteArrayOutputStream.class));

        // Kafka отправляет список новых файлов за текущий год
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(kafkaTemplate).send(
                eq("download_nodelists_is_finished_topic"),
                eq(String.valueOf(currentYear)),
                listCaptor.capture()
        );
        List<String> sent = listCaptor.getValue();
        assertEquals(2, sent.size());
        assertTrue(sent.contains(valid1));
        assertTrue(sent.contains(valid2));

        // Соединение закрывается в finally
        verify(ftpClient).close();
    }

    @Test
    void updateNodelistsWhenDownloadFromYearInFutureReturnsEarly() throws Exception {
        int currentYear = Year.now().getValue();

        setField(service, "ftpPath", "/nodehist/");
        setField(service, "downloadFromYear", currentYear + 1);
        setField(service, "bucket", "nodehist");

        assertThrows(NodelistUpdateException.class, () -> service.updateNodelists());

        // Не происходит никаких действий кроме попытки закрыть FTP в finally
        verify(minioUtils, never()).createBucket(anyString());
        verify(ftpClient, never()).open();
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        verify(ftpClient).close();
    }

    @Test
    void updateNodelistsInvalidInputsWrappedIntoNodelistUpdateException() throws Exception {
        // Пустой bucket вызовет IllegalArgumentException в validateInputs(), который должен быть
        // обернут в NodelistUpdateException
        setField(service, "ftpPath", "/nodehist/");
        setField(service, "downloadFromYear", 1984);
        setField(service, "bucket", "");

        NodelistUpdateException ex = assertThrows(NodelistUpdateException.class, () -> service.updateNodelists());
        assertTrue(ex.getMessage().contains("Nodelist update failed"));
        // close должен быть вызван в finally, даже если open не вызывался
        verify(ftpClient).close();
    }

    @Test
    void updateNodelistsAnyExceptionWrappedAndCloseCalled() throws Exception {
        int currentYear = Year.now().getValue();

        setField(service, "ftpPath", "/nodehist/");
        setField(service, "downloadFromYear", currentYear);
        setField(service, "bucket", "nodehist");

        // Бросаем IOException при listFiles
        when(ftpClient.listFiles(anyString())).thenThrow(new IOException("FTP error"));

        // Также нужно чтобы open не упал раньше
        // open() здесь не мокан явно, но UpdateNodelists вызывает ftpClient.open(); ничего не делает — ок.

        NodelistUpdateException ex = assertThrows(NodelistUpdateException.class, () -> service.updateNodelists());
        assertTrue(ex.getMessage().contains("Nodelist update failed"));

        verify(ftpClient).open();
        verify(ftpClient).close();
    }

    @Test
    void processFileTrimsLeadingSlashAndUploads() throws Exception {
        // Настройка базовых полей
        setField(service, "ftpPath", "/nodehist/");
        setField(service, "downloadFromYear", 1984);
        setField(service, "bucket", "nodehist");

        String filePath = "/nodehist/2020/nodelist.001";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(ftpClient.downloadFile(filePath)).thenReturn(baos);

        var method = UpdateNodelists.class.getDeclaredMethod("processFile", String.class);
        method.setAccessible(true);
        method.invoke(service, filePath);

        verify(minioUtils).putObject(eq("nodehist"), eq("nodehist/2020/nodelist.001"), eq(baos));
    }
}