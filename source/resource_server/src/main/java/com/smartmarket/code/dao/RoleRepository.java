package com.smartmarket.code.dao;


import com.smartmarket.code.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value =  "from Role s where (:roleName is null or s.name like %:roleName%) ")
    List<Role> getListRole(@Param("roleName") String roleName);

    @Query(value =  "from Role s ")
    List<Role> getListRoleAll();

//    @Query(value =  "from Role r inner join RoleUser ru on r.id=ru.roleId where ru.isActive = 0 and  ru.userId=:userId  ")
//    List<Role> getListRoleActiveByUser(@Param("userId") Long userId);

    @Query(value =  "from Role s where  s.id in :roleList")
    Set<Role> finByRoleId(@Param("roleList") List<Long> roleList);

    @Query(value =  "from Role s where  s.name =:roleName")
    Set<Role> findByRoleName(@Param("roleName") String roleName);


    @Query(value = "from Role r join RoleUser ru on ru.roleId = r.id where ru.userId=:userId  ")
    public Set<Role> findRoleUserByUserIdActive(Long userId);


}