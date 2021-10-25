package com.smartmarket.code.service;

import com.smartmarket.code.model.User;

import java.util.Map;

public interface UserKafkaService {
    public User createUserKafka(Map<String, Object> keyPairs) ;

    public User updateUserKafka(Map<String, Object> keyPairs) ;

    public int deleteUserKafka(String username) ;

    public int truncateUserKafka() ;
}
