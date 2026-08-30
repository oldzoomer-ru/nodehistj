package ru.oldzoomer.nodehistj_historic_nodelists.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;

@Configuration
public class JdbcDialectConfig {

    @SuppressWarnings("SameReturnValue")
    @Bean JdbcDialect jdbcDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }
}