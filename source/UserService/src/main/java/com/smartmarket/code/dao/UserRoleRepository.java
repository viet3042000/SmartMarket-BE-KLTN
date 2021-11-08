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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	@Query(value = "from UserRole u where u.userName =:userName")
	public Optional<UserRole> findByUserName(@Param("userName") String userName);

//	@Query(value = "from UserRole u where u.userName =:userName and u.roleName=:roleName")
//	public Optional<UserRole> findUserRole(@Param("userName") String userName, @Param("roleName") String roleName);

	@Query(value = "select u.roleName from UserRole u where u.userName =:userName")
	public String findRoleByUserName(@Param("userName") String userName);

	@Query(value = "select * from user_role where role_name =:role_name", nativeQuery = true)
	public List<UserRole> findByRoleName(@Param("role_name") String roleName);

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM user_role where user_name =:user_name", nativeQuery = true)
	public int deleteByUserName(@Param("user_name") String username) ;

}