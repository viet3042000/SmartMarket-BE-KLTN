package com.smartmarket.code.service;

import com.smartmarket.code.model.Url;
import java.util.Set;

public interface UrlService {
    Set<Url> findUrlByUserIdActive(String clientIdName);
    public Set<Url> findUrlByClientId(String userName) ;
}
