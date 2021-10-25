package com.smartmarket.code.service;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateUserRequest;
import com.smartmarket.code.request.UpdateUserRequest;

import java.util.Map;


public interface UserRoleService {

    public UserRole create(BaseDetail<CreateUserRequest> createUserRequestBaseDetail) ;

    public UserRole update(UserRole userRoleUpdate,BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail) throws Exception;

//    public UserRole delete(UserRole id) ;
//
//    public int deleteByUserName(String userName);

}
