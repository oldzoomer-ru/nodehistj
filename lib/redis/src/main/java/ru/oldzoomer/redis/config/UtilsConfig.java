package ru.oldzoomer.redis.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.oldzoomer.redis.ClearRedisCache;

@Configuration
public class UtilsConfig {
    @Bean
    ClearRedisCache clearRedisCache(CacheManager cacheManager) {
        return new ClearRedisCache(cacheManager);
    }
}
