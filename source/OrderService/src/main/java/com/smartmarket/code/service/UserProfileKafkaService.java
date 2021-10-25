package com.smartmarket.code.service;

import com.smartmarket.code.model.UserProfile;
import com.smartmarket.code.model.UserRole;

import java.text.ParseException;
import java.util.Map;

public interface UserProfileKafkaService {
    public UserProfile createUserProfileKafka(Map<String, Object> keyPairs) throws ParseException;

    public UserProfile updateUserProfileKafka(Map<String, Object> keyPairs) throws ParseException;

    public int deleteUserProfileKafka(String username) ;

    public int truncateUserProfileKafka() ;
}
