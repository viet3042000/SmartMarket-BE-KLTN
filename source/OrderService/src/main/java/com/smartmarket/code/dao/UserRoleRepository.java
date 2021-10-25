package com.smartmarket.code.dao;

import com.smartmarket.code.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    @Query(value = "SELECT * FROM user_role WHERE user_name=:user_name", nativeQuery = true)
    public Optional<UserRole> findByUserName(@Param("user_name") String userName);

//    @Modifying(clearAutomatically = true)
//    // Đây là Native SQL --> dùng tên biến theo DB
//    @Query(value = "UPDATE user_role set role_name =:role_name," +
//                   " enabled=:enabled where id = :id",nativeQuery = true)
//    public int updateUserRoleKafka(@Param("role_name") String roleName,
//                                   @Param("enabled") Integer enabled,@Param("id") Long id) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM user_role where user_name =:user_name", nativeQuery = true)
    public int deleteUserRoleByUserName(@Param("user_name") String username) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE user_role",  nativeQuery = true)
    public int truncateUserRoleKafka() ;

}