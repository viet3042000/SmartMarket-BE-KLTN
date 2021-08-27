package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseUserRoleService;
import com.smartmarket.code.service.UserRoleKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class DataBaseUserRoleServiceImp implements DataBaseUserRoleService {

    @Autowired
    UserRoleKafkaService userRoleKafkaService;

    public void createDatabaseUserRole(Map<String, Object> keyPairs) throws ParseException{
        userRoleKafkaService.createUserRoleKafka(keyPairs);
    }

    public void updateDatabaseUserRole(Map<String, Object> keyPairs) throws ParseException{
        Long id = 0L;
        Long userId =0L;
        Long roleId =0L;
        Long enabled =0L;
        String createAt = "";

        //convert string --> date with formart tương ứng
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                id = ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("user_id")) {
                userId = ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("role_id")) {
                roleId = ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("enabled")) {
                enabled = ((Number)keyPairs.get(k)).longValue();
            }
        }
        userRoleKafkaService.updateUserRoleKafka(userId,roleId,enabled,id);
    }

    public void deleteDatabaseUserRole(Map<String, Object> keyPairs){
        Long id = 0L;

        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                id = ((Number)keyPairs.get(k)).longValue();
            }
        }
        userRoleKafkaService.deleteUserRoleKafka(id);
    }

    public void truncateDatabaseUserRole(){
        userRoleKafkaService.truncateUserRoleKafka();
    }
}
