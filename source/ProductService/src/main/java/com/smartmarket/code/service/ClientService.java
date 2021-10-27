package com.smartmarket.code.service;

import com.smartmarket.code.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    public Optional<Client> findByclientName(String clientIdCode);

    public List<Client> getAllCacheListClient() ;
}
