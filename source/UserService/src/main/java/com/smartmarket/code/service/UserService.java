package com.smartmarket.code.service;

import com.smartmarket.code.model.User;
import java.util.Optional;

public interface UserService {

    public User create(User object) ;

    public User update(User object) ;

    public User delete(String username) ;

    Optional<User> findByUsername(String username);

    public Optional<User> findByUserId(Long userId) ;

    public Optional<User> findUserIdByUsername(String username);
}
