package com.smartmarket.code.service;

import com.smartmarket.code.model.Url;
import java.util.Set;

public interface UrlService {
    Set<Url> findUrlByUserIdActive(Long userId);
    public Set<Url> findUrlByClientName(String clientId) ;
}
