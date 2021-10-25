package com.example.authserver.service;

import com.example.authserver.entities.User;

import java.util.Map;

public interface UserKafkaService {
    public User createUserKafka(Map<String, Object> keyPairs) ;

    public User updateUserKafka(Map<String, Object> keyPairs) ;

    public int deleteUserKafka(String username) ;

    public int truncateUserKafka() ;
}