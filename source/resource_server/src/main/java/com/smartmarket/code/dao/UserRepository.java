package com.smartmarket.code.dao;

import com.smartmarket.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


	@Query(value = "from User u where u.username =:username")
	public Optional<User> findByUsername(@Param("username") String username);

	@Query(value = "select u.id from User u where u.username =:username")
	public Long findUserIdByUsername(@Param("username") String username);
	
}