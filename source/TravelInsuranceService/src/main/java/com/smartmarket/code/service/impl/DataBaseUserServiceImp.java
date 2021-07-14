package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseUserServiceImp implements DataBaseUserService {

    @Autowired
    UserKafkaServiceImp userKafkaServiceImp;

    public void createDatabaseUser(String table, Map<String, Object> keyPairs) throws ParseException{
        userKafkaServiceImp.createUserKafka(keyPairs);
    }
    public void updateDatabaseUser(String table, Map<String, Object> keyPairs) throws ParseException{
        String username = "";
        String password = "";
        Long enabled = 0L;

        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                username =(String)  keyPairs.get(k);
            }
            if (k.equals("user_password")) {
                password =(String)  keyPairs.get(k);
            }
            if (k.equals("enabled")) {
                enabled = (((Number)keyPairs.get(k)).longValue());
            }
        }

        userKafkaServiceImp.updateUserKafka(username,password,enabled);
    }
    public void deleteDatabaseUser(String table, Map<String, Object> keyPairs){
        String username = "";
        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                username = (String)  keyPairs.get(k);
            }
        }
        userKafkaServiceImp.deleteUserKafka(username);
    }

    public void truncateDatabaseUser(String table){
        userKafkaServiceImp.truncateUserKafka();
    }
}
