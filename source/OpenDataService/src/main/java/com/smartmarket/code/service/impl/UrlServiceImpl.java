package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UrlRepository;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.model.Url;
import com.smartmarket.code.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    UrlRepository urlRepository ;

//    @Override
//    public Set<Url> findUrlByClientIdActive(Long clientId) {
//        return urlRepository.findUrlByClientIdActive(clientId);
//    }



    @Override
    @Cacheable(cacheNames = "urllistbyusername", key = "#userName"
    )
    public Set<Url> findUrlByUserIdActive(String userName) {
        return urlRepository.findUrlByUserIdActive(userName);
    }


    @Cacheable(cacheNames = "urllistbyclient", key = "#clientIdName")
    public Set<Url> findUrlByClientId(String clientIdName) {
        return urlRepository.findUrlByClientIdActive(clientIdName);
    }

    @CacheEvict(cacheNames = "urllistbyclient", key = "#clientIdName")
    public void evictSingleClientIdCacheValue(String clientIdName) {
    }


    @CachePut(cacheNames = "client" , key = "#clientIdName")
    public Set<Url> updateClientCacheByClientId(String clientIdName) {
        return urlRepository.findUrlByClientIdActive(clientIdName);
    }

//    @Caching(
//            cacheable = @Cacheable(value = "tasks", condition = "!#noCache", key = "'ALL'"),
//            put = @CachePut(value = "tasks", condition = "#noCache", key = "'ALL'"),
//    evict = @CacheEvict(value = "tasks", condition = "#noCache", key = "'ALL'"))
////    It stores the return value within the cache,
////    And it returns a cached value if it’s present
//    public String findAll(boolean noCache) {
//
//        return "test" ;
//    }


//    @CacheEvict("user")
//    public void clearCacheById(int id) {
//    }
//    @CacheEvict(value = "user", allEntries = true)
//    public void clearCache() {
//    }
//    @CachePut(value = "user")
//    public User reloadAndFindUserById(int id) {
//        simulateSlowService();
//        return new User(id, "reload Any name");
//    }
}
