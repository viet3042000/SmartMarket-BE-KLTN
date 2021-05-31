package com.smartmarket.code.service.impl;


import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.DefaultStatisticsService;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class CachingServiceImpl {

    @Autowired
    CacheManager cacheManager;

    public void putToCache(String cacheName, String key, String value) {
        cacheManager.getCache(cacheName).put(key, value);
    }

    public Object getFromCache(String cacheName, Long key) {
//        StatisticsService statisticsService = new DefaultStatisticsService();
//        CacheManager cacheManager1 = (CacheManager) CacheManagerBuilder.newCacheManagerBuilder()
//                .using(statisticsService)
//                .build();

        Object o = cacheManager.getCache("hoptest").get(key);
        return o;
    }

    @CacheEvict(value = "first", key = "#cacheKey")
    public void evictSingleCacheValue(String cacheKey) {
    }

    @CacheEvict(value = "first", allEntries = true)
    public void evictAllCacheValues() {
    }

    public void evictSingleCacheValue(String cacheName, String cacheKey) {
//        cacheManager.getCache()
        cacheManager.getCache(cacheName).evict(cacheKey);
    }

    public void evictAllCacheValues(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .parallelStream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

//    @Scheduled(fixedRate = 6000)
//    public void evictAllcachesAtIntervals() {
//        evictAllCaches();
//    }
}
