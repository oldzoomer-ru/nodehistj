package ru.gavrilovegor519.nodelistj_download_nodelists.util;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class FtpClient {

    @Value("${ftp.host}")
    private String server;

    @Value("${ftp.port}")
    private int port;

    private FTPClient ftp;

    public void open() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        ftp.enterLocalPassiveMode();
        int reply = ftp.getReplyCode();

        ftp.login("anonymous", "anonymous@");

        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
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
