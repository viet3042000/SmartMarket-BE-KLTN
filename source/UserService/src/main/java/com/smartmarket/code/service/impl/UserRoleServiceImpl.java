package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.dao.UserRoleRepository;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateProviderUserRequest;
import com.smartmarket.code.request.CreateUserRequest;
import com.smartmarket.code.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserRole create(BaseDetail<CreateUserRequest> createUserRequestBaseDetail) {
        UserRole userRoleCreate = new UserRole();
        userRoleCreate.setUserName(createUserRequestBaseDetail.getDetail().getUserName());
        userRoleCreate.setRoleName(createUserRequestBaseDetail.getDetail().getRole());
        userRoleCreate.setEnabled(createUserRequestBaseDetail.getDetail().getEnabled());
        userRoleCreate.setCreateDate(new Date());
        return userRoleRepository.save(userRoleCreate);
    }

    @Override
    public UserRole createProviderAdminUser(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) {
        UserRole userRoleCreate = new UserRole();
        userRoleCreate.setUserName(createProviderAdminUserRequestBaseDetail.getDetail().getUserName());
        userRoleCreate.setRoleName(createProviderAdminUserRequestBaseDetail.getDetail().getRole());
        userRoleCreate.setEnabled(createProviderAdminUserRequestBaseDetail.getDetail().getEnabled());
        userRoleCreate.setCreateDate(new Date());
        return userRoleRepository.save(userRoleCreate);
    }

    @Override
    public UserRole update(UserRole userRoleUpdate, Map<String, Object> keyPairs, String requestId) throws Exception {
        for (String k : keyPairs.keySet()) {
            if (k.equals("newRole")) {
                userRoleUpdate.setRoleName((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                userRoleUpdate.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
        }
        return userRoleRepository.save(userRoleUpdate);
    }

}
