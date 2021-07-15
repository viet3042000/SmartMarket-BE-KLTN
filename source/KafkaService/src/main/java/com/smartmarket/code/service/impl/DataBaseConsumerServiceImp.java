package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class DataBaseConsumerServiceImp implements DataBaseConsumerService {

    @Autowired
    ConsumerKafkaServiceImp consumerKafkaServiceImp;

    public void createDatabaseConsumer(String table, Map<String, Object> keyPairs) throws ParseException{
        consumerKafkaServiceImp.createConsumerKafka(keyPairs);
    }
    public void updateDatabaseConsumer(String table, Map<String, Object> keyPairs) throws ParseException{
        String consumerId = "";
        String createAt = "";
        //convert string --> date with formart tương ứng
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        for (String k : keyPairs.keySet()) {
            if (k.equals("consumerId")) {
                consumerId =(String)keyPairs.get(k);
            }
            if (k.equals("created_at")) {
                createAt = (String)keyPairs.get(k);
            }
        }
        consumerKafkaServiceImp.updateConsumerKafka(consumerId, formatter.parse(createAt));
    }
    public void deleteDatabaseConsumer(String table, Map<String, Object> keyPairs){
        String consumerId = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("consumerId")) {
                consumerId =(String) keyPairs.get(k);
            }
        }
        consumerKafkaServiceImp.deleteConsumerKafka(consumerId);
    }
    public void truncateDatabaseConsumer(String table){
        consumerKafkaServiceImp.truncateConsumerKafka();
    }
}
