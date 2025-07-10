package ru.oldzoomer.nodelistj_download_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for nodelist download module.
 * <p>
 * Responsible for:
 * - Starting Spring Boot application context
 * - Configuring auto-configuration and component scanning
 * - Initializing nodelist download services
 */
@SpringBootApplication
public class NodelistjDownloadNodelistsApplication {

	   public static void main(String[] args) {
	       SpringApplication.run(NodelistjDownloadNodelistsApplication.class, args);
	   }

}
