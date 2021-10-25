package com.smartmarket.code.service;

import com.smartmarket.code.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserService {

    public User create(User object) ;

    public User update(User object) throws Exception;

    public User delete(String username) throws Exception;

    Optional<User> findByUsername(String username);

    public Optional<User> findByUserId(Long userId) ;

    public Optional<User> findUserIdByUsername(String username);
}