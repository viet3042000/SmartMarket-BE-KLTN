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
        Number userIdSync = 0;
        String username = "";
        String password = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("username")) {
                username =(String)  keyPairs.get(k);
            }
            if (k.equals("password")) {
                password =(String)  keyPairs.get(k);
            }
            if (k.equals("user_id")) {
//                userIdSync =(Number) keyPairs.get(k);
            }
            if (k.equals("enabled")) {
//                    userIdSync =(Number) keyPairs.get(k);
            }
        }

        userKafkaServiceImp.updateUserKafka(username,password);
    }
    public void deleteDatabaseUser(String table, Map<String, Object> keyPairs){
//        Number userId = 0;
//
//        for (String k : keyPairs.keySet()) {
//            if (k.equals("user_id")) {
//                userId = (Number) keyPairs.get(k);
//            }
//        }

        String username = "";
        for (String k : keyPairs.keySet()) {
            if (k.equals("username")) {
                username = (String)  keyPairs.get(k);
            }
        }
        userKafkaServiceImp.deleteUserKafka(username);
    }

    public void truncateDatabaseUser(String table){
        userKafkaServiceImp.truncateUserKafka();
    }
}
