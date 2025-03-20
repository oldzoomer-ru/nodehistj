package ru.gavrilovegor519.nodehistj_historic_nodelists.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Log4j2
public class ClearRedisCache {
    private final CacheManager cacheManager;

    public void clearAllCache() {
        cacheManager.getCacheNames()
                .parallelStream()
                .forEach(n -> Objects.requireNonNull(cacheManager.getCache(n)).clear());
        log.info("Cache is cleared");
    }
}
