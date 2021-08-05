package com.smartmarket.code.service;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface RoleService {

    public Role create(Role object) ;

    public Role update(Role object) ;

    public Role delete(Long id) ;

    public Page<Role> getList(Pageable pageable);

}
