package com.smartmarket.code.service;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.UserRole;


public interface UserRoleService {

    public UserRole create(UserRole object) ;

    public UserRole update(UserRole object) ;

    public UserRole delete(UserRole id) ;

    public int deleteByUserId(Long id) ;

}
