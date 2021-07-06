package com.smartmarket.code.config;


import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URL;

@Configuration
@EnableCaching
public class BaseConfig {

//    @Bean
//    public CacheManager cacheManager() {
////        URL myUrl = getClass().getResource("/ehcache.xml");
////        XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
////        CacheManager myCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
////
////        return  myCacheManager ;
//
//        CachingProvider provider = Caching.getCachingProvider();
//        CacheManager cacheManager = (CacheManager) provider.getCacheManager();
//        return cacheManager ;
//    }


//    @Bean
//    public CacheManager cacheManager() {
//        return new EhCacheCacheManager(ehCacheCacheManager().getObject());
//    }
//
//    @Bean
//    public EhCacheManagerFactoryBean ehCacheCacheManager() {
//        EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
//        factory.setConfigLocation(new ClassPathResource("ehcache.xml"));
//        factory.setShared(true);
//        return factory;
//    }
}
