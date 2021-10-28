package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.service.RoleKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class RoleKafkaServiceImp implements RoleKafkaService {
    @Autowired
    private RoleRepository roleRepository;

    public Role createRoleKafka(Map<String, Object> keyPairs) throws ParseException {
        Role role = new Role();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                role.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("role_name")) {
                role.setRoleName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                role.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                role.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                role.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return roleRepository.save(role);
    }

    public Role updateRoleKafka(Map<String, Object> keyPairs) throws ParseException{
        Role role = new Role();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                role.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("role_name")) {
                role.setRoleName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                role.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                role.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                role.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return roleRepository.save(role);
    }


    public int deleteRoleKafka(String roleName) {
        return roleRepository.deleteRoleKafka(roleName);
    }

    public int truncateRoleKafka() {
        return roleRepository.truncateRoleKafka();
    }
}
