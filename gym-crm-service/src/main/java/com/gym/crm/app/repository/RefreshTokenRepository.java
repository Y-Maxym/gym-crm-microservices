package com.gym.crm.app.repository;

import com.gym.crm.app.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT r.id FROM RefreshToken r WHERE r.user.username = :username")
    Long findTokenIdByUserUsername(String username);

    @Query("SELECT r.user.username FROM RefreshToken r WHERE r.token = :token")
    String findUsernameByToken(String token);

    void deleteByToken(String token);
}
