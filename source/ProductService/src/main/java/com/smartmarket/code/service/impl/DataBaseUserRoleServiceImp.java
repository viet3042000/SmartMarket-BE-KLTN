package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseUserRoleService;
import com.smartmarket.code.service.UserRoleKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseUserRoleServiceImp implements DataBaseUserRoleService {

    @Autowired
    UserRoleKafkaService userRoleKafkaService;

    public void createDatabaseUserRole(Map<String, Object> keyPairs) throws ParseException{
        userRoleKafkaService.createUserRoleKafka(keyPairs);
    }

    public void updateDatabaseUserRole(Map<String, Object> keyPairs) throws ParseException{
        userRoleKafkaService.updateUserRoleKafka(keyPairs);
    }

    public void deleteDatabaseUserRole(Map<String, Object> keyPairs){
        String username = "";
        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                username = (String) keyPairs.get(k);
            }
        }
        userRoleKafkaService.deleteUserRoleKafka(username);
    }

    public void truncateDatabaseUserRole(){
        userRoleKafkaService.truncateUserRoleKafka();
    }
}
