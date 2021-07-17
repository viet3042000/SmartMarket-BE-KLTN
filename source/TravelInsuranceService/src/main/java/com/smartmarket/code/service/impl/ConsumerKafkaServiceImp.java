package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ConsumerRepository;
import com.smartmarket.code.model.Consumers;
import com.smartmarket.code.service.ConsumerKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class ConsumerKafkaServiceImp implements ConsumerKafkaService {

    @Autowired
    ConsumerRepository consumerRepository;

    public Consumers createConsumerKafka(Map<String, Object> keyPairs) throws ParseException {
        Consumers consumers = new Consumers();
        // 2021-06-10T10:44:46.296034Z
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

        for (String k : keyPairs.keySet()) {
            if (k.equals("created_at")) {
                String createAt = (String)keyPairs.get(k);
                consumers.setCreateAt(formatter.parse(createAt));
            }
            if (k.equals("consumer_id")) {
                consumers.setId((String) keyPairs.get(k));
            }
        }
        return consumerRepository.save(consumers);

    }

    public int updateConsumerKafka(String consumerId,Date createAt){
        return consumerRepository.updateConsumerClientKafka(consumerId,createAt);
    }

    public int deleteConsumerKafka(String consumerId){
        return consumerRepository.deleteConsumerClientKafka(consumerId);
    }

    public int truncateConsumerKafka(){
        return consumerRepository.truncateConsumerClientKafka();
    }
}
