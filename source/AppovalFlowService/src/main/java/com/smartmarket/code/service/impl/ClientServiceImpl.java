package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ClientRepository;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository ;

//    @Override
//    public Set<Url> findUrlByClientIdActive(Long clientId) {
//        return urlRepository.findUrlByClientIdActive(clientId);
//    }

    @Override
    @Cacheable(cacheNames = "client", key = "#clientId")
    public Optional<Client> findByclientName(String clientId) {
        return clientRepository.findByclientName(clientId);
    }


    @CacheEvict(cacheNames = "client", key = "#clientId")
    public void evictSingleClientNameCacheValue(String clientId) {
    }


    @CachePut(cacheNames = "client" , key = "#clientId")
    public Optional<Client> updateClientCacheByClientIdCode(String clientId) {
        return clientRepository.findByclientName(clientId);
    }

    @Override
    @Cacheable(cacheNames = "clientlist")
    public List<Client> getAllCacheListClient() {
        return clientRepository.findAll();
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
