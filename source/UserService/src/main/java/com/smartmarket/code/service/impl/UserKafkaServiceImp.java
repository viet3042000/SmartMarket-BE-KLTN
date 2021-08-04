package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ClientRepository;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.model.User;
import com.smartmarket.code.service.UserKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserKafkaServiceImp implements UserKafkaService {
    @Autowired
    private UserRepository userRepository;

    public User createUserKafka(Map<String, Object> keyPairs) {
        User user = new User();

        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                user.setUsername((String) keyPairs.get(k));
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
        return userRepository.updateConsumerClientKafka(username, password,enabled);
    }

    public int deleteUserKafka(String username) {
        return userRepository.deleteConsumerClientKafka(username);
    }

    public int truncateUserKafka() {
        return userRepository.truncateConsumerClientKafka();
    }

}