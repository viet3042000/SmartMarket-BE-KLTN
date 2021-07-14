package com.smartmarket.code.service;

import com.smartmarket.code.model.Client;

import java.util.Map;

public interface ClientKafkaService {
    public Client createConsumerClientKafka(Map<String, Object> keyPairs) ;

    public int updateConsumerClientKafka(String clientIdName,String clientIdCode, String secret ,Long isActive,
                                         String consumerId,String ipAccess) ;

    public int deleteConsumerClientKafka(String clientIdName) ;

    public int truncateConsumerClientKafka() ;
}
