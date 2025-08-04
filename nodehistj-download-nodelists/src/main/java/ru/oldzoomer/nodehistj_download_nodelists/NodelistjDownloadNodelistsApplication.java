package ru.oldzoomer.nodehistj_download_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for nodelist download module.
 * <p>
 * Responsible for:
 * - Starting Spring Boot application context
 * - Configuring autoconfiguration and component scanning
 * - Initializing nodelist download services
 */
@SpringBootApplication
public class NodelistjDownloadNodelistsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodelistjDownloadNodelistsApplication.class, args);
    }

}
