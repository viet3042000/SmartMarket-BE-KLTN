package com.smartmarket.code.dao;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	@Query(value = "from User u where u.id =:id")
	public Optional<User> findByUserId(@Param("id") Long id);

}