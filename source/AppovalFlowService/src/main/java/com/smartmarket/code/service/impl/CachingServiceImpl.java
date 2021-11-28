package com.smartmarket.code.service.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.cache.Caching;
import javax.cache.management.CacheStatisticsMXBean;
import javax.cache.spi.CachingProvider;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;


@Service
public class CachingServiceImpl {

    @Autowired
    CacheManager cacheManager;

    public void putToCache(String cacheName, String key, String value) {
        cacheManager.getCache(cacheName).put(key, value);
    }
//
    public Object getFromCache(String cacheName, Long key) {
//        StatisticsService statisticsService = new DefaultStatisticsService();
//        CacheManager cacheManager1 = (CacheManager) CacheManagerBuilder.newCacheManagerBuilder()
//                .using(statisticsService)
//                .build();

        Object o = cacheManager.getCache(cacheName).get(key);
        return o;
    }


    public Object getFromCacheString(String cacheName, String key) {
        Object o =  cacheManager.getCache(cacheName).get(key) == null  ? null : cacheManager.getCache(cacheName).get(key).get();
        return o;
    }
//
    @CacheEvict(value = "first", key = "#cacheKey")
    public void evictSingleCacheValue(String cacheKey) {
    }
//
    @CacheEvict(value = "first", allEntries = true)
    public void evictAllCacheValues() {
    }
//
    public void evictSingleCacheValue(String cacheName, String cacheKey) {
//        cacheManager.getCache()
        cacheManager.getCache(cacheName).evict(cacheKey);
    }
//
    public void evictAllCacheValues(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }
//
    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .parallelStream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }


    public static CacheStatisticsMXBean getCacheStatisticsMXBean(final String cacheName) {
        final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = null;
        try {
            name = new ObjectName("*:type=CacheStatistics,*,Cache=" + cacheName);
        } catch (MalformedObjectNameException ex) {
//            LOG.error("Someting wrong with ObjectName {}", ex);
            ex.printStackTrace();
        }
        Set<ObjectName> beans = mbeanServer.queryNames(name, null);
        if (beans.isEmpty()) {
//            LOG.debug("Cache Statistics Bean not found");
//            ex.printStackTrace();
            return null;
        }
        ObjectName[] objArray = beans.toArray(new ObjectName[beans.size()]);
        return JMX.newMBeanProxy(mbeanServer, objArray[0], CacheStatisticsMXBean.class);
    }

//    @Scheduled(fixedRate = 6000)
//    public void evictAllcachesAtIntervals() {
//        evictAllCaches();
//    }
}
