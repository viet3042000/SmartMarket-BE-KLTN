package com.smartmarket.code.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class MySecurityService {
    public boolean hasPermissionCustom(Authentication authentication, String userName) {
        String name = authentication.getName() ;
        return name.equals(userName);
    }
}
