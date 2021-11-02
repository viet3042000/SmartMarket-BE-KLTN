package com.smartmarket.code.service;

import com.smartmarket.code.model.UserProductProvider;

import java.util.Optional;

public interface UserProductProviderService {

    Optional<UserProductProvider> findByProductProviderName(String productProviderName);

    Optional<UserProductProvider> findByUserName(String productProviderName);
}
