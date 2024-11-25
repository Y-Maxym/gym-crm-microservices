package com.gym.crm.app.repository;

import com.gym.crm.app.entity.JwtBlackToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface JwtBlackTokenRepository extends JpaRepository<JwtBlackToken, Long> {

    void deleteByExpiryDateBefore(LocalDateTime expiryDate);

    boolean existsByToken(String token);
}
