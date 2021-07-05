package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ConsumerRepository;
import com.smartmarket.code.model.Consumers;
import com.smartmarket.code.service.ConsumerKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConsumerKafkaServiceImp implements ConsumerKafkaService {

    @Autowired
    ConsumerRepository consumerRepository;

    public Consumers createConsumerKafka(Map<String, Object> keyPairs) {
        Consumers consumers = new Consumers();

        for (String k : keyPairs.keySet()) {
            if (k.equals("consumer_id_sync")) {
                consumers.setConsumerIdSync((String) keyPairs.get(k));
            }
            if (k.equals("created_at")) {
                consumers.setCreateAt((String) keyPairs.get(k));
            }
            if (k.equals("consumerId")) {
                consumers.setId((String) keyPairs.get(k));
            }
        }
        return consumerRepository.save(consumers);

    }

    public int updateConsumerKafka(String consumerIdSync,String createAt){
        return consumerRepository.updateConsumerClientKafka(consumerIdSync,createAt);
    }

    public int deleteConsumerKafka(String consumerIdSync){
        return consumerRepository.deleteConsumerClientKafka(consumerIdSync);
    }

    public int truncateConsumerKafka(){
        return consumerRepository.truncateConsumerClientKafka();
    }
}
