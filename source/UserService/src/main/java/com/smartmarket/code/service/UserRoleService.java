package com.smartmarket.code.service;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.UserRole;


public interface UserRoleService {

    public UserRole create(UserRole object) ;

    public UserRole update(String userName, String role, Long enabled) throws Exception;

    public UserRole delete(UserRole id) ;

    public int deleteByUserName(String userName);

}
