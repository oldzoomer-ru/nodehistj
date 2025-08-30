package ru.oldzoomer.nodehistj_history_diff.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.cql.generator.CreateKeyspaceCqlGenerator;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for creating Cassandra keyspace.
 * <p>
 * Responsible for:
 * - Creating keyspace if it doesn't exist
 * - Configuring Cassandra connection properties
 * - Setting up keyspace creation conditions
 * <p>
 * This configuration class ensures that the required Cassandra keyspace exists
 * before the application starts, allowing proper database operations.
 */
@Configuration
@ConditionalOnClass(CqlSession.class)
@ConditionalOnProperty(name = "spring.cassandra.create-keyspace", havingValue = "true")
@AutoConfigureBefore(CassandraAutoConfiguration.class)
@Slf4j
public class CassandraCreateKeyspaceAutoConfiguration {
    /**
     * Constructor for CassandraCreateKeyspaceAutoConfiguration.
     *
     * @param cqlSessionBuilder CQL session builder
     * @param properties Cassandra properties
     */
    public CassandraCreateKeyspaceAutoConfiguration(
            CqlSessionBuilder cqlSessionBuilder,
            CassandraProperties properties) {
        try (CqlSession session = cqlSessionBuilder.withKeyspace((CqlIdentifier) null).build()) {
            log.info("Creating keyspace {} ...", properties.getKeyspaceName());
            session.execute(CreateKeyspaceCqlGenerator.toCql(
                    CreateKeyspaceSpecification.createKeyspace(properties.getKeyspaceName()).ifNotExists()));
        }
    }
}