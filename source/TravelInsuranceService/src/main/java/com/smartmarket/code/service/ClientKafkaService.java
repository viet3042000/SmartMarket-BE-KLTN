package com.smartmarket.code.service;

import com.smartmarket.code.model.Client;

import java.util.Map;

public interface ClientKafkaService {
    public Client createConsumerClientKafka(Map<String, Object> keyPairs) ;

    public int updateConsumerClientKafka(Long id,String clientId,String secret,String consumerId) ;

    public int deleteConsumerClientKafka(String clientId) ;

    public int truncateConsumerClientKafka() ;
}
