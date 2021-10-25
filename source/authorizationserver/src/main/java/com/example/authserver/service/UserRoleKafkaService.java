package com.example.authserver.service;

import com.example.authserver.entities.UserRole;

import java.text.ParseException;
import java.util.Map;

public interface UserRoleKafkaService {
    public UserRole createUserRoleKafka(Map<String, Object> keyPairs) throws ParseException;

    public UserRole updateUserRoleKafka(Map<String, Object> keyPairs)throws ParseException;

    public int deleteUserRoleKafka(String username) ;

    public int truncateUserRoleKafka() ;
}
