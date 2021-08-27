package com.smartmarket.code.dao;

import com.smartmarket.code.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Transactional
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query(value = "from UserRole u where u.id =:id")
    public Optional<UserRole> findByUserRoleId(@Param("id") Long id);

    @Query(value = "select u.roleId from UserRole u where u.userId =:userId")
    public ArrayList<Long> findListRoleByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    // Đây là Native SQL --> dùng tên biến theo DB
    @Query(value = "UPDATE user_role set user_id =:user_id, role_id =:role_id," +
                   " enabled=:enabled where id = :id",nativeQuery = true)
    public int updateUserRoleKafka(@Param("user_id") Long userId, @Param("role_id") Long roleId,
                                   @Param("enabled") Long enabled,@Param("id") Long id) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM user_role where id =:id", nativeQuery = true)
    public int deleteUserRoleById(@Param("id") Long id) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE user_role",  nativeQuery = true)
    public int truncateUserRoleKafka() ;

}