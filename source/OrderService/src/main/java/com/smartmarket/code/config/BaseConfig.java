package com.smartmarket.code.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

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
