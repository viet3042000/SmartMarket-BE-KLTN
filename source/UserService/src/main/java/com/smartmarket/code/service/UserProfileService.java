package com.smartmarket.code.service;

import com.smartmarket.code.model.User;
import com.smartmarket.code.model.UserProfile;

import java.util.Optional;

public interface UserProfileService {

    public UserProfile create(UserProfile object) ;

    public UserProfile update(UserProfile object ,String userNameUpdate) ;

    public UserProfile deleteByUserName(String userName) ;

    Optional<UserProfile> findByUsername(String username);

}
