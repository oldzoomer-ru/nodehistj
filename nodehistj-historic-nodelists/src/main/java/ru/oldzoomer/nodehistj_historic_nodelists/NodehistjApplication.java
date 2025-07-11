package ru.oldzoomer.nodehistj_historic_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for working with historic nodelists.
 * Launches Spring Boot application with enabled transaction management.
 */
@SpringBootApplication
@EnableTransactionManagement
public class NodehistjApplication {

    /**
     * Application entry point.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(NodehistjApplication.class, args);
    }
}
