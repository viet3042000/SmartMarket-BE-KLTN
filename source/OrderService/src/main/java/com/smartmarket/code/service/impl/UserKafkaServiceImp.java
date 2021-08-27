package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.User;
import com.smartmarket.code.service.UserKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserKafkaServiceImp implements UserKafkaService {
    @Autowired
    private UserRepository userRepository;

    public User createUserKafka(Map<String, Object> keyPairs) {
        User user = new User();

        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                String userName = (String) keyPairs.get(k);
                Optional<User> u = userRepository.findByUsername(userName);
                if(u.isPresent()) {
                    throw new CustomException("User is exist", HttpStatus.BAD_REQUEST, null ,null,null, null, HttpStatus.BAD_REQUEST);
                }else {
                    user.setUsername(userName);
                }
            }
            if (k.equals("user_password")) {
                user.setPassword((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                user.setEnabled(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("id")) {
                user.setId(((Number)keyPairs.get(k)).longValue());
            }

        }
        return userRepository.save(user);
    }

    public int updateUserKafka(String username,String password,Long enabled) {
        return userRepository.updateUserKafka(username, password,enabled);
    }

    public int deleteUserKafka(String username) {
        return userRepository.deleteUserKafka(username);
    }

    public int truncateUserKafka() {
        return userRepository.truncateUserKafka();
    }

}
