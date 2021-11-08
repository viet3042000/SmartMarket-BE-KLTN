package com.smartmarket.code.dao;

import com.smartmarket.code.model.User;
import com.smartmarket.code.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    // Đây là JPQL (Hibernate) --> dùng tên biến của User
    @Query(value = "from UserProfile u where u.userName =:userName")
    public Optional<UserProfile> findByUsername(@Param("userName") String userName);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM user_profile where user_name =:user_name", nativeQuery = true)
    public int deleteByUserName(@Param("user_name") String username) ;

}