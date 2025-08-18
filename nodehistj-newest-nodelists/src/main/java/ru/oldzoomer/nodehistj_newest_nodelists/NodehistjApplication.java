package ru.oldzoomer.nodehistj_newest_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for working with current nodelists.
 * <p>
 * Responsible for Spring Boot application startup and transaction configuration.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCassandraRepositories(basePackages = "ru.oldzoomer.nodehistj_newest_nodelists.repo")
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
