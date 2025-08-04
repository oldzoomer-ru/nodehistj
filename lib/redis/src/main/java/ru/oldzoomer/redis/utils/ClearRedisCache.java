package ru.oldzoomer.redis.utils;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClearRedisCache {
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 3 * * *")
    public void clearCache() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> {
                    log.debug("Clearing cache: {}", cacheName);
                    Cache cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                    } else {
                        log.warn("Cache {} not found", cacheName);
                    }
                });
    }
}