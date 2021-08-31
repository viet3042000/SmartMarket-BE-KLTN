package com.smartmarket.code.service;

import com.smartmarket.code.model.UserProfile;

import java.text.ParseException;
import java.util.Map;

public interface UserProfileKafkaService {
    public UserProfile createUserProfileKafka(Map<String, Object> keyPairs) throws ParseException;

    public int updateUserProfileKafka(Map<String, Object> keyPairs) throws ParseException;

    public int deleteUserProfileKafka(Long id) ;

    public int truncateUserProfileKafka() ;
}
