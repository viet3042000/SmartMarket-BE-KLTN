package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface AuthorizationService {
    boolean AuthorUserAccess(Long userId) ;
    String getTokenFromDatabase() throws JsonProcessingException ;
}
