package com.smartmarket.code.service;

import com.smartmarket.code.model.UserProductProvider;

import java.util.Optional;

public interface UserProductProviderService {
    public UserProductProvider create(String userName, String productProviderName) ;

    Optional<UserProductProvider> findByProductProviderName(String productProviderName);

    Optional<UserProductProvider> findByUserName(String productProviderName);
}
