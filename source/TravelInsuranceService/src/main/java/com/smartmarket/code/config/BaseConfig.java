package com.smartmarket.code.config;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
@EnableCaching
public class BaseConfig {

//    @Bean(destroyMethod = "close")
//    public CacheManager cacheManager() {
//        URL myUrl = getClass().getResource("/my-config.xml");
//        XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
//        CacheManager myCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
//
//        return  myCacheManager ;
//    }
}
