package ru.gavrilovegor519.nodelistj_download_nodelists.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FtpClient {

    @Value("${ftp.host}")
    private String server;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.user}")
    private String user;

    @Value("${ftp.password}")
    private String password;

    private FTPClient ftp;

    public void open() throws IOException {
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        try {
            ftp.connect(server, port);
            if (!ftp.login(user, password)) {
                throw new IOException("FTP login failed");
            }

            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new IOException("FTP server refused connection");
            }

            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public String[] listFiles(String path) throws IOException {
        return ftp.listNames(path);
    }

    public ByteArrayOutputStream downloadFile(String source) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ftp.retrieveFile(source, out);
        return out;
    }

    public void close() throws IOException {
        if (ftp != null) {
            ftp.disconnect();
        }
    }
}
