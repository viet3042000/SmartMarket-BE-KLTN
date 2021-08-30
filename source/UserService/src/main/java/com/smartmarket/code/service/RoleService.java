package com.smartmarket.code.service;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.UpdateRoleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface RoleService {

    public Role create(Role object) ;

    public Role update(BaseDetail<UpdateRoleRequest> updateRoleRequestBaseDetail) throws Exception;

    public Role deleteByRoleId(Long id) throws Exception;

    public Role deleteByRoleName(String roleName) throws Exception;

    public Page<Role> getList(Pageable pageable);

}
