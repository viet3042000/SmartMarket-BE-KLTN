package com.smartmarket.code.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    @PreAuthorize("hasAnyAuthority('CREATOR')")
    public String getRoleCreator() {
        return "get Role CREATOR";
    }
}
