package com.smartmarket.code.dao;

import com.smartmarket.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


	@Query(value = "from User u where u.userName =:userName")
	public Optional<User> findByUsername(@Param("userName") String userName);

	@Query(value = "select u.id from User u where u.userName =:userName")
	public Long findUserIdByUsername(@Param("userName") String userName);
	
}