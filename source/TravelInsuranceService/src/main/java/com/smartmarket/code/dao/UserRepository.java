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
public interface UserRepository extends JpaRepository<User, Integer> {

	// Đây là JPQL (Hibernate) --> dùng tên biến của User
	@Query(value = "from User u where u.username =:username")
	public Optional<User> findByUsername(@Param("username") String username);

	@Query(value = "select u.id from User u where u.username =:username")
	public Long findUserIdByUsername(@Param("username") String username);


	@Modifying(clearAutomatically = true)
	// Đây là Native SQL --> dùng tên biến theo DB
	@Query(value = "UPDATE users set user_name =:user_name, password =:password where user_id_sync = :userIdSync",nativeQuery = true)
	public int updateConsumerClientKafka(@Param("userIdSync") Number userIdSync, @Param("user_name") String username,
										 @Param("password") String password) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM users where user_id_sync =:userIdSync", nativeQuery = true)
	public int deleteConsumerClientKafka(@Param("userIdSync") Number userIdSync) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "TRUNCATE TABLE users",  nativeQuery = true)
	public int truncateConsumerClientKafka() ;
	
}