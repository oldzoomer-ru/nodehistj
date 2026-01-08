package ru.oldzoomer.nodehistj_download_nodelists.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 * Utility class for handling FTP operations.
 * <p>
 * Provides functionality for:
 * - Connecting to FTP servers
 * - Listing files in directories
 * - Downloading files
 * - Managing FTP connections
 * <p>
 * Uses Apache Commons Net library for FTP operations.
 */
@Component
@Log4j2
public class FtpClient implements AutoCloseable {

    @Value("${ftp.host}")
    private String server;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.user}")
    private String user;

    @Value("${ftp.password}")
    private String password;

    private FTPClient ftp;
    
    /**
     * Opens connection to FTP server
     * @throws IOException if connection fails
     */
    public void open() throws IOException {
        log.debug("Opening FTP connection to {}:{}", server, port);
        if (ftp != null) {
            // Close existing connection if present
            close();
        }
        ftp = new FTPClient();
        try {
            ftp.connect(server, port);
            log.debug("Connected to FTP server");
            
            if (!ftp.login(user, password)) {
                throw new IOException("FTP login failed for user: " + user);
            }
            log.debug("Logged in to FTP server successfully");

            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new IOException("FTP server refused connection, reply code: " + ftp.getReplyCode());
            }

            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            log.debug("FTP connection configured successfully");
        } catch (IOException e) {
            log.error("Failed to open FTP connection to {}:{}", server, port, e);
            close(); // Ensure cleanup on failure
            throw e;
        }
    }

    /**
     * Gets list of files in specified FTP directory
     * @param path path to directory on FTP server
     * @return array of file names
     * @throws IOException if operation fails
     */
    public String[] listFiles(String path) throws IOException {
        log.debug("Listing files in FTP directory: {}", path);
        try {
            String[] files = ftp.listNames(path);
            if (files == null) {
                log.warn("No files found in directory: {}", path);
                return new String[0];
            }
            log.debug("Found {} files in directory: {}", files.length, path);
            return files;
        } catch (IOException e) {
            log.error("Failed to list files in FTP directory: {}", path, e);
            throw e;
        }
    }

    /**
     * Downloads file from FTP server
     * @param source path to file on FTP server
     * @return stream with file content
     * @throws IOException if download fails
     */
    public ByteArrayOutputStream downloadFile(String source) throws IOException {
        log.debug("Downloading file from FTP: {}", source);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean success = ftp.retrieveFile(source, out);
            if (!success) {
                throw new IOException("Failed to download file from FTP: " + source);
            }
            log.debug("Successfully downloaded file from FTP: {}", source);
            return out;
        } catch (IOException e) {
            log.error("Failed to download file from FTP: {}", source, e);
            throw e;
        }
    }

    /**
     * Closes connection to FTP server
     * @throws IOException if closing fails
     */
    @Override
    public void close() throws IOException {
        if (ftp != null) {
            try {
                ftp.disconnect();
                log.debug("FTP connection disconnected successfully");
            } catch (IOException e) {
                log.warn("Error while disconnecting from FTP server", e);
                throw e;
            } finally {
                ftp = null;
            }
        }
    }
}
