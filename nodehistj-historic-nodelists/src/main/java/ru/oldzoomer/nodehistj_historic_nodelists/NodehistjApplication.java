package ru.oldzoomer.nodehistj_historic_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for working with historic nodelists.
 * Launches Spring Boot application with enabled transaction management.
 */
@SpringBootApplication
@EnableCaching
public class NodehistjApplication {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    static void main(String[] args) {
        SpringApplication.run(NodehistjApplication.class, args);
    }

}
