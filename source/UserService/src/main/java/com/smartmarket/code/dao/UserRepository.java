package com.smartmarket.code.dao;

import com.smartmarket.code.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, String> {

	// Đây là JPQL (Hibernate) --> dùng tên biến của User
	@Query(value = "select * from users u"  , nativeQuery = true )
	public Page<User> findAllUser(Pageable pageable);

	// Đây là JPQL (Hibernate) --> dùng tên biến của User
	@Query(value = "from User u where u.userName =:username")
	public Optional<User> findByUsername(@Param("username") String username);

	// Đây là JPQL (Hibernate) --> dùng tên biến của User
	@Query(value = "from User u where u.userName =:username")
	public Optional<User> checkUserExist(@Param("username") String username);

	@Query(value = "from User u where u.id =:id")
	public Optional<User> findByUserId(@Param("id") Long id);

	@Query(value = "from User u where u.userName =:username")
	public Optional<User> findUserByUsername(@Param("username") String username);


	@Modifying(clearAutomatically = true)
	// Đây là Native SQL --> dùng tên biến theo DB
	@Query(value = "UPDATE users set user_password =:password, enabled =:enabled where user_name = :user_name",nativeQuery = true)
	public int updateConsumerClientKafka(@Param("user_name") String username,@Param("password") String password,
										 @Param("enabled") Long enabled) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM users where user_name =:user_name", nativeQuery = true)
	public int deleteConsumerClientKafka(@Param("user_name") String username) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "TRUNCATE TABLE users",  nativeQuery = true)
	public int truncateConsumerClientKafka() ;
	
}