package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserRoleRepository;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class UserRoleServiceImp implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    public void createUserRole(Map<String, Object> keyPairs) throws ParseException{
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
        userRoleRepository.save(userRole);
    }

    public void updateUserRole(Map<String, Object> keyPairs) throws ParseException{
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
        userRoleRepository.save(userRole);
    }

    public void deleteUserRole(Map<String, Object> keyPairs){
        String username = "";
        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                username = (String) keyPairs.get(k);
            }
        }
        userRoleRepository.deleteUserRoleByUserName(username);
    }

    public void truncateUserRole(){
        userRoleRepository.truncateUserRoleKafka();
    }
}
