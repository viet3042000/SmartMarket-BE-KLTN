package com.smartmarket.code.service;

import com.smartmarket.code.model.UserProfile;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateProviderUserRequest;
import com.smartmarket.code.request.CreateUserRequest;
import com.smartmarket.code.request.UpdateUserRequest;

import java.util.Map;
import java.util.Optional;

public interface UserProfileService {

    public UserProfile create(BaseDetail<CreateUserRequest> createUserRequestBaseDetail) ;

    public UserProfile createProviderAdminUser(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) ;

    public UserProfile update(UserProfile userProfile, Map<String, Object> keyPairs) ;

//    public UserProfile deleteByUserName(String userName) ;

    Optional<UserProfile> findByUsername(String username);

}
