package ru.oldzoomer.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConnection {
    @Bean
    @ConfigurationProperties("spring.data.redis")
    @ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
    RedisConnectionProperties redisConnectionProperties() {
        return new RedisConnectionProperties();
    }

    @Bean
    RedisConnectionFactory redisConnectionFactory(
        RedisConnectionProperties redisConnectionProperties
    ) {
        return new LettuceConnectionFactory(
            new RedisStandaloneConfiguration(
                redisConnectionProperties.getHost(),
                redisConnectionProperties.getPort()
            )
        );
    }
}
