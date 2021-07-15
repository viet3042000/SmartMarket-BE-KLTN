package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ClientDetailRepository;
import com.smartmarket.code.dao.ClientRepository;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.model.ClientDetail;
import com.smartmarket.code.service.ClientDetailService;
import com.smartmarket.code.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientDetailServiceImpl implements ClientDetailService {

    @Autowired
    ClientDetailRepository clientDetailRepository ;

    @Override
    @Cacheable(cacheNames = "clientdetail", key = "#clientId")
    public Optional<ClientDetail> findByclientIdName(String clientId) {
        return clientDetailRepository.findByclientIdName(clientId);
    }

    @CacheEvict(cacheNames = "clientdetail", key = "#clientId")
    public void evictSingleClientNameCacheValue(String clientId) {
    }

    @CachePut(cacheNames = "clientdetail" , key = "#clientId")
    public Optional<ClientDetail> updateClientCacheByClientIdCode(String clientId) {
        return clientDetailRepository.findByclientIdName(clientId);
    }


}
