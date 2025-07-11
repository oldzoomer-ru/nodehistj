package ru.oldzoomer.nodehistj_newest_nodelists.util;

import java.util.Objects;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
@Profile("!test")
public class ClearRedisCache {
    private final CacheManager cacheManager;

    public void clearAllCache() {
        cacheManager.getCacheNames()
                .parallelStream()
                .forEach(n -> Objects.requireNonNull(cacheManager.getCache(n)).clear());
        log.info("Cache is cleared");
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        clearAllCache();
    }
}
