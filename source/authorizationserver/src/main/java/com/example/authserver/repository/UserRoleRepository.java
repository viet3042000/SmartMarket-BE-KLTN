package com.example.authserver.repository;

import com.example.authserver.entities.UserRole;
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

    @Query(value = "from UserRole u where u.id =:id")
    public Optional<UserRole> findByUserRoleId(@Param("id") Long id);

    @Query(value = "from UserRole u where u.userName =:userName")
    public UserRole findByUserName(@Param("userName") String userName);

    @Query(value = "select u.roleName from UserRole u where u.userName =:userName")
    public String findRoleByUserName(@Param("userName") String userName);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM user_role where user_name =:user_name", nativeQuery = true)
    public int deleteUserRoleByUserName(@Param("user_name") String username) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE user_role",  nativeQuery = true)
    public int truncateUserRoleKafka() ;

}