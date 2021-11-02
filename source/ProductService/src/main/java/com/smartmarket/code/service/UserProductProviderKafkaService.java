package com.smartmarket.code.service;

import com.smartmarket.code.model.UserProductProvider;

import java.text.ParseException;
import java.util.Map;

public interface UserProductProviderKafkaService {
    public UserProductProvider createUserProductProviderKafka(Map<String, Object> keyPairs) throws ParseException;

    public UserProductProvider updateUserProductProviderKafka(Map<String, Object> keyPairs) throws ParseException;

    public int deleteUserProductProviderKafka(String userName) ;

    public int truncateUserProductProviderKafka() ;
}
