package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserProfileRepository;
import com.smartmarket.code.service.DataBaseUserProfileService;
import com.smartmarket.code.service.UserProfileKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseUserProfileServiceImp implements DataBaseUserProfileService {
    @Autowired
    UserProfileKafkaService userProfileKafkaService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public void createDatabaseUserProfile(Map<String, Object> keyPairs) throws ParseException {
        userProfileKafkaService.createUserProfileKafka(keyPairs);
    }

    public void updateDatabaseUserProfile(Map<String, Object> keyPairs) throws ParseException{
        userProfileKafkaService.updateUserProfileKafka(keyPairs);
    }

    public void deleteDatabaseUserProfile(Map<String, Object> keyPairs){
        Long id = 0L;

        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                id = ((Number)keyPairs.get(k)).longValue();
            }
        }
        userProfileKafkaService.deleteUserProfileKafka(id);
    }

    public void truncateDatabaseUserProfile(){
        userProfileKafkaService.truncateUserProfileKafka();
    }

}