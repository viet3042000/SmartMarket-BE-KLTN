package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.dao.UserProfileRepository;
import com.smartmarket.code.service.DataBaseRoleService;
import com.smartmarket.code.service.RoleKafkaService;
import com.smartmarket.code.service.UserProfileKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseRoleServiceImp implements DataBaseRoleService {
    @Autowired
    RoleKafkaService roleKafkaService;

    @Autowired
    private RoleRepository roleRepository;

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
