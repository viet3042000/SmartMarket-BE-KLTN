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
import java.util.Optional;

@Transactional
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	@Query(value = "from UserRole u where u.id =:id")
	public Optional<UserRole> findByUserRoleId(@Param("id") Long id);

	@Query(value = "select u.roleId from UserRole u where u.userId =:userId")
	public ArrayList<Long> findListRoleByUserId(@Param("userId") Long userId);

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM user_role where user_id =:userId", nativeQuery = true)
	public int deleteUserRoleByUserId(@Param("userId") Long userId) ;

}