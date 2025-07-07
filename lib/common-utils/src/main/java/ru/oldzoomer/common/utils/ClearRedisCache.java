package ru.oldzoomer.common.utils;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class ClearRedisCache {
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 3 * * *")
    public void clearCache() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> {
                    log.info("Clearing cache: {}", cacheName);
                    cacheManager.getCache(cacheName).clear();
                });
    }
}