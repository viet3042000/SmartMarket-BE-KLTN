package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;

public interface AuthorizationService {
    boolean AuthorUserAccess(Long userId) ;
    String getTokenOncePerRequest() throws JsonProcessingException, APIAccessException;
    String getToken() throws JsonProcessingException, APIAccessException;
    public boolean validActuator(String[] paths , String urlRequest) ;
}
