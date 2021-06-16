package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APITimeOutRequestException;

public interface AuthorizationService {
    boolean AuthorUserAccess(Long userId) ;
    String getTokenFromDatabase() throws JsonProcessingException, APITimeOutRequestException;
    public boolean validActuator(String[] paths , String urlRequest) ;
}
