package com.gym.crm.app.repository;

import com.gym.crm.app.entity.JwtBlackToken;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Rollback
class JwtBlackTokenRepositoryTest extends AbstractTestRepository<JwtBlackTokenRepository> {

    @Test
    @DisplayName("Test token exists functionality")
    void givenToken_whenExistsByToken_thenReturnTrue() {
        // given
        JwtBlackToken token = EntityTestData.getValidJwtBlackToken();
        entityManager.persist(token);

        // when
        boolean exists = repository.existsByToken(token.getToken());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Test token does not exist functionality")
    void givenNonExistentToken_whenExistsByToken_thenReturnFalse() {
        // given
        String nonExistentToken = "non_existent_token";

        // when
        boolean exists = repository.existsByToken(nonExistentToken);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Test delete by expiry date before functionality")
    void givenExpiredTokens_whenDeleteByExpiryDateBefore_thenTokensDeleted() {
        // given
        JwtBlackToken expiredToken = EntityTestData.getExpiredJwtBlackToken();
        JwtBlackToken validToken = EntityTestData.getValidJwtBlackToken();

        entityManager.persist(expiredToken);
        entityManager.persist(validToken);

        // when
        repository.deleteByExpiryDateBefore(LocalDateTime.now());

        // then
        assertThat(repository.existsByToken(expiredToken.getToken())).isFalse();
        assertThat(repository.existsByToken(validToken.getToken())).isTrue();
    }
}