package com.smartmarket.code.service;

import com.smartmarket.code.model.Url;

import java.util.Set;

public interface UrlService {
//    Set<Url> findUrlByClientIdActive(Long clientId);

    Set<Url> findUrlByUserIdActive(Long userId);


}
