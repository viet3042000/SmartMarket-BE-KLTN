package com.example.authserver.service.Impl;

import com.example.authserver.repository.RoleRepository;
import com.example.authserver.service.DataBaseRoleService;
import com.example.authserver.service.RoleKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseRoleServiceImp implements DataBaseRoleService {
    @Autowired
    RoleKafkaService roleKafkaService;


    public void createDatabaseRole(Map<String, Object> keyPairs) throws ParseException {
        roleKafkaService.createRoleKafka(keyPairs);
    }

    public void updateDatabaseRole(Map<String, Object> keyPairs) throws ParseException{
        roleKafkaService.updateRoleKafka(keyPairs);
    }

    public void deleteDatabaseRole(Map<String, Object> keyPairs){
        String roleName = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("role_name")) {
                roleName = (String) keyPairs.get(k);
            }
        }
        roleKafkaService.deleteRoleKafka(roleName);
    }

    public void truncateDatabaseRole(){
        roleKafkaService.truncateRoleKafka();
    }
}