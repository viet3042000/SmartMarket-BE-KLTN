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
            if (k.equals("username")) {
                user.setUsername((String) keyPairs.get(k));
            }
            if (k.equals("password")) {
                user.setPassword((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                user.setEnabled(Integer.parseInt((String) keyPairs.get(k)));
            }
            if (k.equals("user_id")) {
                user.setUserIdSync((Integer) keyPairs.get(k));
            }

        }
        return userRepository.save(user);
    }

    public int updateUserKafka(Number userIdSync,String username,String password) {
        return userRepository.updateConsumerClientKafka(userIdSync,username, password);
    }

    public int deleteUserKafka(Number userIdSync) {
        return userRepository.deleteConsumerClientKafka(userIdSync);
    }

    public int truncateUserKafka() {
        return userRepository.truncateConsumerClientKafka();
    }

}
