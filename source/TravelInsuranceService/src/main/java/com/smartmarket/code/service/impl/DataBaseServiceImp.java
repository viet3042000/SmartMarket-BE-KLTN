package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataBaseServiceImp implements DataBaseService {
    @Autowired
    ClientKafkaServiceImp clientKafkaServiceImp;

    @Autowired
    UserKafkaServiceImp userKafkaServiceImp;

    @Autowired
    ConsumerKafkaServiceImp consumerKafkaServiceImp;

    public void createDatabase(String table, Map<String, Object> keyPairs){
        //Create table with key-value in afterObj to TravelInsurance DB that match with key-pair in sourceObj
        //key-pair in sourceObj ("table","schema", "name")-->"name"."schema"."table" (=topic name)
        //VD: postgres.public.clients
        if(table.equals("clients")) {
            clientKafkaServiceImp.createConsumerClientKafka(keyPairs);
        }
        if(table.equals("users")){
            userKafkaServiceImp.createUserKafka(keyPairs);
        }
        if(table.equals("consumers")){
            consumerKafkaServiceImp.createConsumerKafka(keyPairs);
        }
    }

    public void updateDatabase(String table, Map<String, Object> keyPairs){
        //Update table with key-value in afterObj to TravelInsurance DB that match with key-pair in sourceObj
        if(table.equals("clients")) {
            String clientIdSync = "";
            String clientIdCode = "";
            String secret = "";
            Long isActive = 0L;
            String consumerId = "";
            String ipAccess = "";

            for (String k : keyPairs.keySet()) {
                if (k.equals("client_id_sync")) {
                    clientIdSync =(String) keyPairs.get(k);
                }
                if (k.equals("client_id_code")) {
                    clientIdCode =(String) keyPairs.get(k);
                }
                if (k.equals("secret")) {
                    secret =(String) keyPairs.get(k);
                }
                if (k.equals("is_active")) {
                    isActive = (Long) keyPairs.get(k);
                }
                if (k.equals("consumer_id")) {
                    consumerId =(String)  keyPairs.get(k);
                }
                if (k.equals("ip_access")) {
                    ipAccess = (String)  keyPairs.get(k);
                }
            }
            clientKafkaServiceImp.updateConsumerClientKafka(clientIdSync,clientIdCode,secret ,
                    isActive, ipAccess);
        }
        if(table.equals("users")){
            Number userIdSync = 0;
            String username = "";
            String password = "";

            for (String k : keyPairs.keySet()) {
                if (k.equals("user_name")) {
                    username =(String)  keyPairs.get(k);
                }
                if (k.equals("user_password")) {
                    password =(String)  keyPairs.get(k);
                }
                if (k.equals("id")) {
                    userIdSync =(Number) keyPairs.get(k);
                }
                if (k.equals("enabled")) {
//                    userIdSync =(Number) keyPairs.get(k);
                }
            }

            userKafkaServiceImp.updateUserKafka(userIdSync,username,password);
        }
        if(table.equals("consumers")){
            String consumerIdSync = "";
            String createAt = "";

            for (String k : keyPairs.keySet()) {
                if (k.equals("consumer_id_sync")) {
                    consumerIdSync =(String)  keyPairs.get(k);
                }
                if (k.equals("created_at")) {
                    createAt =(String)  keyPairs.get(k);
                }
            }
            consumerKafkaServiceImp.updateConsumerKafka(consumerIdSync, createAt);
        }
    }

    public void deleteDatabase(String table, Map<String, Object> keyPairs){
        if(table.equals("clients")) {
            Number clientIdSync = 0;

            for (String k : keyPairs.keySet()) {
                if (k.equals("client_id_sync")) {
                    clientIdSync =(Number) keyPairs.get(k);
                }
            }
            clientKafkaServiceImp.deleteConsumerClientKafka(clientIdSync);
        }
        if(table.equals("users")){
            Number userId = 0;

            for (String k : keyPairs.keySet()) {
                if (k.equals("id")) {
                    userId = (Number) keyPairs.get(k);
                }
            }
            userKafkaServiceImp.deleteUserKafka(userId);
        }
        if(table.equals("consumers")){
            String consumerIdSync = "";

            for (String k : keyPairs.keySet()) {
                if (k.equals("consumer_id_sync")) {
                    consumerIdSync =(String) keyPairs.get(k);
                }
            }
            consumerKafkaServiceImp.deleteConsumerKafka(consumerIdSync);
        }
    }

    public void truncateDatabase(String table){
        if(table.equals("clients")) {
            clientKafkaServiceImp.truncateConsumerClientKafka();
        }
        if(table.equals("users")){
            userKafkaServiceImp.truncateUserKafka();
        }
        if(table.equals("consumers")){
            consumerKafkaServiceImp.truncateConsumerKafka();
        }
    }
}
