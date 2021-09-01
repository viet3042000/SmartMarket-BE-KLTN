package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserRoleRepository;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.service.UserRoleKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class UserRoleKafkaServiceImp implements UserRoleKafkaService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserRole createUserRoleKafka(Map<String, Object> keyPairs) throws ParseException {
        UserRole userRole = new UserRole();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                userRole.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("user_name")) {
                userRole.setUserName((String) keyPairs.get(k));
            }
            if (k.equals("role_name")) {
                userRole.setRoleName((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                userRole.setEnabled(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("create_date")) {
                String createAt = (String)keyPairs.get(k);
                userRole.setCreateDate(formatter.parse(createAt));
            }
        }
        return userRoleRepository.save(userRole);
    }


    public int updateUserRoleKafka(Map<String, Object> keyPairs) {
        Long id = 0L;
        String roleName ="";
        Long enabled =0L;

        //convert string --> date with formart tương ứng
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                id = ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("role_name")) {
                roleName = (String) keyPairs.get(k);
            }
            if (k.equals("enabled")) {
                enabled = ((Number)keyPairs.get(k)).longValue();
            }
        }
        return userRoleRepository.updateUserRoleKafka(roleName, enabled,id);
    }

    public int deleteUserRoleKafka(Long id) {
        return userRoleRepository.deleteUserRoleById(id);
    }

    public int truncateUserRoleKafka() {
        return userRoleRepository.truncateUserRoleKafka();
    }
}
