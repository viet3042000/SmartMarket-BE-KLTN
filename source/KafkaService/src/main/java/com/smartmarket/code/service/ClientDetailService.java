package com.smartmarket.code.service;

import com.smartmarket.code.model.Client;
import com.smartmarket.code.model.ClientDetail;

import java.util.List;
import java.util.Optional;

public interface ClientDetailService {
    public Optional<ClientDetail> findByclientIdName(String clientId);
}
