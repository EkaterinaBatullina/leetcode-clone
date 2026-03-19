package com.technokratos.submissionserviceimpl.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        CaffeineCache testcasesCache = new CaffeineCache("testcases",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .expireAfterAccess(Duration.ofMinutes(5))
                        .build());
        CaffeineCache infoCache = new CaffeineCache("info",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .expireAfterAccess(Duration.ofMinutes(5))
                        .build());
        CaffeineCache submissionsCache = new CaffeineCache("submissions",
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(Duration.ofMinutes(15))
                        .expireAfterAccess(Duration.ofMinutes(10))
                        .build());
        cacheManager.setCaches(List.of(testcasesCache, infoCache, submissionsCache));
        return cacheManager;
    }
}

