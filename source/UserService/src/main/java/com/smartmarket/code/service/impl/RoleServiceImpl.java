package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.User;
import com.smartmarket.code.service.RoleService;
import com.smartmarket.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role create(Role object) {
        object.setEnabled(Constant.STATUS.ACTIVE);
        object.setRoleName(object.getRoleName());
        return roleRepository.save(object);
    }

    @Override
    public Role update(Role object) {
        Role roleUpdate = roleRepository.findById(object.getId()).orElse(null);
        if (roleUpdate != null) {
            object.setEnabled(object.getEnabled());
            object.setRoleName(object.getRoleName());
        }
        roleRepository.save(object);
        return roleUpdate;
    }

    @Override
    public Role delete(Long id) {
        Role roleDelete = roleRepository.findById(id).orElse(null);
        if (roleDelete != null) {
            roleRepository.delete(roleDelete);
        }
        return roleDelete;
    }
}
