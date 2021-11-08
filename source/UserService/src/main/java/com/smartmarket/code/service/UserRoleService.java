package com.smartmarket.code.service;

import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateProviderUserRequest;
import com.smartmarket.code.request.CreateUserRequest;
import com.smartmarket.code.request.UpdateUserRequest;

import java.util.Map;


public interface UserRoleService {

    public UserRole create(BaseDetail<CreateUserRequest> createUserRequestBaseDetail) ;

    public UserRole createProviderAdminUser(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) ;

    public UserRole update(UserRole userRoleUpdate, Map<String, Object> keyPairs, String requestId) throws Exception;

}
