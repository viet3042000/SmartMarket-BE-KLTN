package com.smartmarket.code.service;

import com.smartmarket.code.model.User;

import java.util.Map;

public interface UserKafkaService {
    public User createUserKafka(Map<String, Object> keyPairs) ;

    public int updateUserKafka(Number userIdSync,String username,String password) ;

    public int deleteUserKafka(Number userIdSync) ;

    public int truncateUserKafka() ;
}
