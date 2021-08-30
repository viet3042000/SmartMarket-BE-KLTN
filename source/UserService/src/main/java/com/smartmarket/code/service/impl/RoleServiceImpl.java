package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.UpdateRoleRequest;
import com.smartmarket.code.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role create(Role object) {
        object.setEnabled(Constant.STATUS.ACTIVE);
        return roleRepository.save(object);
    }

    @Override
    public Role update(BaseDetail<UpdateRoleRequest> updateRoleRequestBaseDetail) throws Exception {
//        Role roleUpdate = roleRepository.findById(updateRoleRequestBaseDetail.getDetail().getRole().getId()).orElse(null);
        Role roleUpdate = roleRepository.findByUserRoleName(updateRoleRequestBaseDetail.getDetail().getRole().getRoleName()).orElse(null);
        if (roleUpdate != null) {
//            roleUpdate.setRoleName(updateRoleRequestBaseDetail.getDetail().getRole().getRoleName());
            roleUpdate.setDesc(updateRoleRequestBaseDetail.getDetail().getRole().getDesc());
            roleUpdate.setEnabled(updateRoleRequestBaseDetail.getDetail().getRole().getEnabled());
            roleRepository.save(roleUpdate);
        }else {
            throw new Exception("Role_id is not exist");
        }
        return roleUpdate;
    }

    @Override
    public Role deleteByRoleId(Long id) throws Exception {
        Role roleDelete = roleRepository.findById(id).orElse(null);
        if (roleDelete != null) {
            roleRepository.delete(roleDelete);
        }else {
            throw new Exception("Role_id is not exist");
        }
        return roleDelete;
    }

    @Override
    public Role deleteByRoleName(String roleName) throws Exception {
//        Role roleDelete = roleRepository.findById(id).orElse(null);
        Role roleDelete = roleRepository.findByUserRoleName(roleName).orElse(null);
        if (roleDelete != null) {
            roleRepository.delete(roleDelete);
        }else {
            throw new Exception("Role_name is not exist");
        }
        return roleDelete;
    }


    @Override
    public Page<Role> getList(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
}
