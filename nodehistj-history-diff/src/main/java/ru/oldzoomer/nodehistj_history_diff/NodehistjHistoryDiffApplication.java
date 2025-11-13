package ru.oldzoomer.nodehistj_history_diff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Main application class for analyzing differences between nodelist versions.
 * <p>
 * Features include:
 * <ul>
 *   <li>REST API for retrieving node change history</li>
 *   <li>Comparison of different nodelist versions</li>
 *   <li>Redis caching of results</li>
 * </ul>
 */
@SpringBootApplication
@EnableCassandraRepositories(basePackages = "ru.oldzoomer.nodehistj_history_diff.repo")
@EnableCaching
public class NodehistjHistoryDiffApplication {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(NodehistjHistoryDiffApplication.class, args);
    }

}