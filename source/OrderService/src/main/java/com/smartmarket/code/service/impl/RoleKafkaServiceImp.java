package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.dao.UserProfileRepository;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.UserProfile;
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
                role.setEnabled(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                role.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return roleRepository.save(role);
    }

    public int updateRoleKafka(Map<String, Object> keyPairs) throws ParseException{
        String roleName="";
        String desc="";
        Long enabled =0L;

        //convert string --> date with formart tương ứng
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("role_name")) {
                roleName = (String) keyPairs.get(k);
            }
            if (k.equals("description")) {
                desc = (String) keyPairs.get(k);
            }
            if (k.equals("enabled")) {
                enabled = ((Number)keyPairs.get(k)).longValue();
            }
        }
        return roleRepository.updateRoleKafka(roleName,desc,enabled);
    }


    public int deleteRoleKafka(String roleName) {
        return roleRepository.deleteRoleKafka(roleName);
    }


    public int truncateRoleKafka() {
        return roleRepository.truncateRoleKafka();
    }
}
