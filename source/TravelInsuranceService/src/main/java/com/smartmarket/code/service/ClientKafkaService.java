package com.smartmarket.code.service;

import com.smartmarket.code.model.Client;

import java.util.Map;

public interface ClientKafkaService {
    public Client createConsumerClientKafka(Map<String, Object> keyPairs) ;

    public int updateConsumerClientKafka(String clientIdSync,String clientIdCode, String secret ,Long isActive,
                                         String ipAccess) ;

    public int deleteConsumerClientKafka(Number clientIdSync) ;

    public int truncateConsumerClientKafka() ;
}
