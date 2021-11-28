package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.model.User;
import com.smartmarket.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    public void createUser(Map<String, Object> keyPairs) throws ParseException{
        User user = new User();

        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                user.setUserName((String) keyPairs.get(k));
            }
            if (k.equals("user_password")) {
                user.setPassword((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                user.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("id")) {
                user.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("email")) {
                user.setEmail((String) keyPairs.get(k));
            }
            if (k.equals("oauth_provider")) {
                user.setOauthProvider((String) keyPairs.get(k));
            }
        }
        userRepository.save(user);
    }

    public void updateUser(Map<String, Object> keyPairs) throws ParseException{
        User user = new User();

        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                user.setUserName((String) keyPairs.get(k));
            }
            if (k.equals("user_password")) {
                user.setPassword((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                user.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("id")) {
                user.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("email")) {
                user.setEmail((String) keyPairs.get(k));
            }
            if (k.equals("oauth_provider")) {
                user.setOauthProvider((String) keyPairs.get(k));
            }
        }
        userRepository.save(user);
    }

    public void deleteUser(Map<String, Object> keyPairs){
        String username = "";
        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                username = (String) keyPairs.get(k);
            }
        }
        userRepository.deleteUserKafka(username);
    }

    public void truncateUser(){
        userRepository.truncateUserKafka();
    }
}
