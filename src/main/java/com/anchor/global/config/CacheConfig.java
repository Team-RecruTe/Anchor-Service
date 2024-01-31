package com.anchor.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

  @Bean
  public CacheManager cacheManager(List<CaffeineCache> caffeineCaches) {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(caffeineCaches);
    return cacheManager;
  }

  @Bean
  public List<CaffeineCache> caffeineCaches() {
    List<CaffeineCache> caches = Arrays.stream(CacheType.values())
        .map(cache -> new CaffeineCache(
            cache.getName(),
            Caffeine.newBuilder()
                .expireAfterWrite(cache.getExpireAfterWrite(), TimeUnit.SECONDS)
                .maximumSize(cache.getMaximumSize())
                .recordStats()
                .build()
        ))
        .toList();
    return caches;
  }

  @Getter
  public enum CacheType {
    TOP_MENTORING("topMentoring", 3_600, 100),
    TOP_TAG("topTag", 3_600, 100);

    private final String name;
    private final int expireAfterWrite;
    private final int maximumSize;

    CacheType(String name, int expireAfterWrite, int maximumSize) {
      this.name = name;
      this.expireAfterWrite = expireAfterWrite;
      this.maximumSize = maximumSize;
    }

  }

}
