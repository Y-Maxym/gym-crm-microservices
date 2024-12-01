package com.gym.crm.microservices.gateway.service.security.repository;

import com.gym.crm.microservices.gateway.service.security.entity.JwtBlackToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtBlackTokenRepository extends JpaRepository<JwtBlackToken, Long> {

    boolean existsByToken(String token);
}
