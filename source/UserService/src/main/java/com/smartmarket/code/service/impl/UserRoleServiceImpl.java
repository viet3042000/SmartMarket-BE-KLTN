package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.dao.UserRoleRepository;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;


    @Override
    public UserRole create(UserRole object) {
        object.setRoleName(object.getRoleName());
        object.setUserName(object.getUserName());
        object.setEnabled(Constant.STATUS.ACTIVE);
        object.setCreateDate(new Date());
        return userRoleRepository.save(object);
    }

    @Override
    public UserRole update(String userName, String role, Long enabled) throws Exception {
        UserRole userRoleUpdate = userRoleRepository.findByUserName(userName).orElse(null);
        if (userRoleUpdate != null) {
            userRoleUpdate.setRoleName(role);
            userRoleUpdate.setEnabled(enabled);
        }else{
            throw new Exception("User_name is not exist");
        }
        return userRoleRepository.save(userRoleUpdate);
    }

    @Override
    public UserRole delete(UserRole id) {
        return null;
    }

    @Override
    public int deleteByUserName(String userName) {
        return userRoleRepository.deleteUserRoleByUserName(userName);
    }
}
