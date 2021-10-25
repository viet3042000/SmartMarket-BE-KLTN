package com.example.authserver.service.Impl;

import com.example.authserver.entities.UserRole;
import com.example.authserver.repository.UserRoleRepository;
import com.example.authserver.service.UserRoleKafkaService;
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
                userRole.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("create_date")) {
                String createAt = (String)keyPairs.get(k);
                userRole.setCreateDate(formatter.parse(createAt));
            }
        }
        return userRoleRepository.save(userRole);
    }


    public UserRole updateUserRoleKafka(Map<String, Object> keyPairs)throws ParseException {
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
                userRole.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("create_date")) {
                String createAt = (String)keyPairs.get(k);
                userRole.setCreateDate(formatter.parse(createAt));
            }
        }
        return userRoleRepository.save(userRole);
    }

    public int deleteUserRoleKafka(String username) {
        return userRoleRepository.deleteUserRoleByUserName(username);
    }

    public int truncateUserRoleKafka() {
        return userRoleRepository.truncateUserRoleKafka();
    }
}