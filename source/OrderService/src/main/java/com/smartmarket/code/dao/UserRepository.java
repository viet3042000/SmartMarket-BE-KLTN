package com.smartmarket.code.dao;

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
public interface UserRepository extends JpaRepository<User, String> {


	@Query(value = "select * from users where user_name =:user_name",nativeQuery = true)
	public Optional<User> findByUsername(@Param("user_name") String username);

	@Query(value = "select u.id from User u where u.userName =:username")
	public Long findUserIdByUsername(@Param("username") String username);


//	@Modifying(clearAutomatically = true)
//	// Đây là Native SQL --> dùng tên biến theo DB
//	@Query(value = "UPDATE users set user_password =:password, enabled =:enabled, email =:email, provider =:provider where user_name = :user_name",nativeQuery = true)
//	public int updateUserKafka(@Param("user_name") String username, @Param("password") String password,
//							   @Param("enabled") Integer enabled, @Param("email") String email,
//							   @Param("provider") String provider) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM users where user_name =:user_name", nativeQuery = true)
	public int deleteUserKafka(@Param("user_name") String username) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "TRUNCATE TABLE users",  nativeQuery = true)
	public int truncateUserKafka() ;
	
}