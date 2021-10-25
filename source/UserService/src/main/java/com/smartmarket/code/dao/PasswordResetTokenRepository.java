package com.smartmarket.code.dao;

import com.smartmarket.code.model.PasswordResetToken;
import com.smartmarket.code.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    @Query(value = "from PasswordResetToken u where u.token =:token")
    public Optional<PasswordResetToken> findByToken(@Param("token") String token);
}
