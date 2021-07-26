package com.smartmarket.code.service;

import com.smartmarket.code.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    Long findUserIdByUsername(String username);
}
