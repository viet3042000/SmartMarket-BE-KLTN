package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseUserProductProviderService;
import com.smartmarket.code.service.UserProductProviderKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseUserProductProviderServiceImpl implements DataBaseUserProductProviderService {
    @Autowired
    UserProductProviderKafkaService userProductProviderKafkaService;

    public void createDatabaseUserProductProvider(Map<String, Object> keyPairs) throws ParseException {
        userProductProviderKafkaService.createUserProductProviderKafka(keyPairs);
    }

    public void updateDatabaseUserProductProvider(Map<String, Object> keyPairs) throws ParseException{
        userProductProviderKafkaService.updateUserProductProviderKafka(keyPairs);
    }

    public void deleteDatabaseUserProductProvider(Map<String, Object> keyPairs){
        String userName = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                userName = (String) keyPairs.get(k);
            }
        }
        userProductProviderKafkaService.deleteUserProductProviderKafka(userName);
    }

    public void truncateDatabaseUserProductProvider(){
        userProductProviderKafkaService.truncateUserProductProviderKafka();
    }
}
