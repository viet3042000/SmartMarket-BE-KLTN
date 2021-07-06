package com.smartmarket.code.service;


import com.smartmarket.code.model.Consumers;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public interface ConsumerKafkaService {

    public Consumers createConsumerKafka(Map<String, Object> keyPairs) throws ParseException;

    public int updateConsumerKafka(String consumerIdSync, Date createAt) ;

    public int deleteConsumerKafka(String consumerIdSync) ;

    public int truncateConsumerKafka() ;

}
