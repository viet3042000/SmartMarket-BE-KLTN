package com.smartmarket.code.service;

import com.smartmarket.code.model.Client;
import com.smartmarket.code.model.Url;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClientService {
    public Optional<Client> findByclientName(String clientId);

    public List<Client> getAllCacheListClient() ;
}
