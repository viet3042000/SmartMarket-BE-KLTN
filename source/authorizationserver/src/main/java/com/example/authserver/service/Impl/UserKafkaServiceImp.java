package com.example.authserver.service.Impl;

import com.example.authserver.entities.User;
import com.example.authserver.repository.UserRepository;
import com.example.authserver.service.UserKafkaService;
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
            if (k.equals("provider")) {
                user.setProvider((String) keyPairs.get(k));
            }
        }
        return userRepository.save(user);
    }

    public User updateUserKafka(Map<String, Object> keyPairs) {
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
            if (k.equals("provider")) {
                user.setProvider((String) keyPairs.get(k));
            }
        }
        return userRepository.save(user);
    }

    public int deleteUserKafka(String username) {
        return userRepository.deleteUserKafka(username);
    }

    public int truncateUserKafka() {
        return userRepository.truncateUserKafka();
    }

}