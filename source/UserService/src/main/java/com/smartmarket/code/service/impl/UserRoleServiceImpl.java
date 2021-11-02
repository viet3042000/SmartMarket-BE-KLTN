package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserRoleRepository;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateProviderUserRequest;
import com.smartmarket.code.request.CreateUserRequest;
import com.smartmarket.code.request.UpdateUserRequest;
import com.smartmarket.code.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;


    @Override
    public UserRole create(BaseDetail<CreateUserRequest> createUserRequestBaseDetail) {
        UserRole userRoleCreate = new UserRole();
        userRoleCreate.setUserName(createUserRequestBaseDetail.getDetail().getUser().getUserName());
        userRoleCreate.setRoleName(createUserRequestBaseDetail.getDetail().getRole());
        userRoleCreate.setEnabled(createUserRequestBaseDetail.getDetail().getUser().getEnabled());
        userRoleCreate.setCreateDate(new Date());
        return userRoleRepository.save(userRoleCreate);
    }

    @Override
    public UserRole createProviderAdminUser(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) {
        UserRole userRoleCreate = new UserRole();
        userRoleCreate.setUserName(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getUserName());
        userRoleCreate.setRoleName(createProviderAdminUserRequestBaseDetail.getDetail().getRole());
        userRoleCreate.setEnabled(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getEnabled());
        userRoleCreate.setCreateDate(new Date());
        return userRoleRepository.save(userRoleCreate);
    }

    @Override
    public UserRole update(UserRole userRoleUpdate,BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail) throws Exception {
        userRoleUpdate.setRoleName(updateUserRequestBaseDetail.getDetail().getRole());
        userRoleUpdate.setEnabled(updateUserRequestBaseDetail.getDetail().getUser().getEnabled());
        return userRoleRepository.save(userRoleUpdate);
    }

//    @Override
//    public UserRole delete(UserRole id) {
//        return null;
//    }
//
//    @Override
//    public int deleteByUserName(String userName) {
//        return userRoleRepository.deleteUserRoleByUserName(userName);
//    }
}
