package com.smartmarket.code.dao;

import com.smartmarket.code.model.AccessToken;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Transactional
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    @Query(value = "from AccessToken at where at.userName = :userName")
    public AccessToken findByUsername(@Param("userName") String userName);

    @Modifying(clearAutomatically = true)
    @Query(value = "update access_token  set expire_time = :expireTime, " +
            "            issue_time = :issueTime , " +
            "            token = :token " +
            "            where access_token_id = :id " , nativeQuery = true)
    int updateTokenByAccessTokenId(@Param("expireTime") Long expireTime ,@Param("issueTime") Long issueTime ,
                                         @Param("token") String token, @Param("id") Long id);

}