package com.smartmarket.code.service;


import com.smartmarket.code.model.Consumers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public interface ConsumerKafkaService {

    public Consumers createConsumerKafka(Map<String, Object> keyPairs) ;

    public int updateConsumerKafka(String consumerIdSync,String createAt) ;

    public int deleteConsumerKafka(String consumerIdSync) ;

    public int truncateConsumerKafka() ;

}
