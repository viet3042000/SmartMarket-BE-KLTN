package com.smartmarket.code.service;


import com.smartmarket.code.model.Consumers;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public interface ConsumerKafkaService {

    public Consumers createConsumerKafka(Map<String, Object> keyPairs) throws ParseException;

    public int updateConsumerKafka(String consumerId, Date createAt) ;

    public int deleteConsumerKafka(String consumerId) ;

    public int truncateConsumerKafka() ;

}
