package ru.oldzoomer.nodehistj_newest_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for working with current nodelists.
 * <p>
 * Responsible for Spring Boot application startup and transaction configuration.
 */
@SpringBootApplication
@EnableCaching
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
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
