package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.dao.UserRoleRepository;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.service.RoleService;
import com.smartmarket.code.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;


    @Override
    public UserRole create(UserRole object) {
        object.setRoleId(object.getRoleId());
        object.setUserId(object.getUserId());
        object.setEnabled(Constant.STATUS.ACTIVE);
        return userRoleRepository.save(object);
    }

    @Override
    public UserRole update(UserRole object) {
//        UserRole userRoleUpdate = userRoleRepository.findById(object.getId()).orElse(null);
//        if (userRoleUpdate != null) {
//            object.setEnabled(object.getEnabled());
//            object.set(object.getRoleName());
//        }
//        roleRepository.save(object);
        return null;

    }

    @Override
    public UserRole delete(UserRole id) {
        return null;
    }

    @Override
    public int deleteByUserId(Long id) {
        return userRoleRepository.deleteUserRoleByUserId(id);
    }
}
