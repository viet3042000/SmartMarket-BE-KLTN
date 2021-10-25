package com.example.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authserver.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	User findByUserName(String username);

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM users where user_name =:user_name", nativeQuery = true)
	public int deleteUserKafka(@Param("user_name") String username) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "TRUNCATE TABLE users",  nativeQuery = true)
	public int truncateUserKafka() ;
}

