package com.smartmarket.code.service;

import com.smartmarket.code.model.User;
import java.util.Optional;

public interface UserService {

    public User create(User object) ;

    public User update(User object) ;

    public User delete(Long id) ;

    Optional<User> findByUsername(String username);

    Long findUserIdByUsername(String username);
}
