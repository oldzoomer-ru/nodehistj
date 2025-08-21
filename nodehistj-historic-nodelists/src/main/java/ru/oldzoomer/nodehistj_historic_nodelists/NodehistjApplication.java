package ru.oldzoomer.nodehistj_historic_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Main application class for working with historic nodelists.
 * Launches Spring Boot application with enabled transaction management.
 */
@SpringBootApplication
@EnableCassandraRepositories(basePackages = "ru.oldzoomer.nodehistj_historic_nodelists.repo")
@EnableCaching
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
