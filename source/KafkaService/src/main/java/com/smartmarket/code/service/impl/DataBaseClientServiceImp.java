package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseClientServiceImp implements DataBaseClientService {

    @Autowired
    ClientKafkaServiceImp clientKafkaServiceImp;

    public void createDatabaseClient(String table, Map<String, Object> keyPairs) throws ParseException {
        clientKafkaServiceImp.createConsumerClientKafka(keyPairs);
    }

    public void updateDatabaseClient(String table, Map<String, Object> keyPairs) throws ParseException{
        Long id = 0L;
        String clientId = "";
        String secret = "";
        String consumerId = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                id = ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("client_id")) {
                clientId =(String) keyPairs.get(k);
            }
            if (k.equals("client_secret")) {
                secret =(String) keyPairs.get(k);
            }
            if (k.equals("consumer_id")) {
                consumerId =(String) keyPairs.get(k);
            }
            if (k.equals("consumer_id")) {
                consumerId =(String) keyPairs.get(k);
            }

        }
        clientKafkaServiceImp.updateConsumerClientKafka(id,clientId,secret,consumerId);
    }

    public void deleteDatabaseClient(String table, Map<String, Object> keyPairs){
        String clientId = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("client_id")) {
                clientId =(String) keyPairs.get(k);
            }
        }
        clientKafkaServiceImp.deleteConsumerClientKafka(clientId);
    }

    public void truncateDatabaseClient(String table){
        clientKafkaServiceImp.truncateConsumerClientKafka();
    }

}
