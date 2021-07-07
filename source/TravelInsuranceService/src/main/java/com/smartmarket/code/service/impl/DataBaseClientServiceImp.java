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
        Long clientId = 0L;
        String clientIdCode = "";
        String secret = "";
        Long isActive = 0L;
        String consumerId = "";
        String ipAccess = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("client_id")) {
                clientId =((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("client_id_code")) {
                clientIdCode =(String) keyPairs.get(k);
            }
            if (k.equals("secret")) {
                secret =(String) keyPairs.get(k);
            }
            if (k.equals("is_active")) {
                isActive = ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("consumer_id")) {
                consumerId =(String) keyPairs.get(k);
            }
            if (k.equals("ip_access")) {
                ipAccess = (String)  keyPairs.get(k);
            }
        }
        clientKafkaServiceImp.updateConsumerClientKafka(clientId,clientIdCode,secret ,
                isActive,consumerId, ipAccess);
    }

    public void deleteDatabaseClient(String table, Map<String, Object> keyPairs){
        Long clientId = 0L;

        for (String k : keyPairs.keySet()) {
            if (k.equals("client_id")) {
                clientId =((Number)keyPairs.get(k)).longValue();
            }
        }
        clientKafkaServiceImp.deleteConsumerClientKafka(clientId);
    }

    public void truncateDatabaseClient(String table){
        clientKafkaServiceImp.truncateConsumerClientKafka();
    }

}
