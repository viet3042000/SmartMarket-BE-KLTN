package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.model.User;
import com.smartmarket.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository ;


    @Override
    public User create(User object) {
        object.setEnabled(Constant.STATUS.ACTIVE);
        object.setPassword(object.getPassword());
        object.setUserName(object.getUserName());

        return userRepository.save(object);
    }

    @Override
    public User update(User object) {
        User userUpdate = userRepository.findByUserId(object.getId()).orElse(null);
        if (userUpdate != null) {
            object.setEnabled(object.getEnabled());
            object.setPassword(object.getPassword());
            object.setUserName(object.getUserName());
        }
        userRepository.save(object);
        return userUpdate;

    }

    @Override
    public User delete(Long id) {
        User userDelete = userRepository.findByUserId(id).orElse(null);
        if (userDelete != null) {
            userRepository.delete(userDelete);
        }
        return userDelete;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Long findUserIdByUsername(String username) {
        return userRepository.findUserIdByUsername(username);
    }
}
