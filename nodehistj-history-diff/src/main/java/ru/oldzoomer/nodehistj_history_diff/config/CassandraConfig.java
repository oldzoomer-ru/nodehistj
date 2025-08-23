package ru.oldzoomer.nodehistj_history_diff.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.stereotype.Component;

@Component
public class CassandraConfig extends AbstractCassandraConfiguration {
    @Value("${cassandra.keyspace-name:nodehistj_newest}")
    private String keyspaceName;

    @Value("${cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${cassandra.port:9042}")
    private int port;

    @Value("${cassandra.local-datacenter:datacenter1}")
    private String localDataCenter;

    @Value("${cassandra.schema-action:CREATE_IF_NOT_EXISTS}")
    private SchemaAction schemaAction;

    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected String getLocalDataCenter() {
        return localDataCenter;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return schemaAction;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(CreateKeyspaceSpecification
                .createKeyspace(keyspaceName)
                .ifNotExists()
                .withSimpleReplication());
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[] {
            "ru.oldzoomer.nodehistj_history_diff.entity"
        };
    }
}