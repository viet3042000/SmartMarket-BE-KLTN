package com.smartmarket.code.dao;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.User;
import com.smartmarket.code.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface RoleRepository extends JpaRepository<Role,String> {

    @Query(value = "from Role u where u.roleName =:roleName")
    public Optional<Role> findByRoleName(@Param("roleName") String roleName);

}