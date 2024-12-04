package com.gym.crm.microservices.authservice.repository;

import com.gym.crm.microservices.authservice.entity.RefreshToken;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Rollback
class RefreshTokenRepositoryTest extends AbstractTestRepository<RefreshTokenRepository> {

    @Test
    @DisplayName("Test find refresh token by token functionality")
    public void givenToken_whenFindByToken_thenTokenIsFound() {
        // given
        RefreshToken expected = EntityTestData.getTransientValidRefreshToken();
        entityManager.persist(expected.getUser());
        entityManager.persist(expected);

        // when
        Optional<RefreshToken> actual = repository.findByToken(expected.getToken());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find refresh token by incorrect token functionality")
    public void givenIncorrectToken_whenFindByToken_thenTokenIsNotFound() {
        // when
        Optional<RefreshToken> actual = repository.findByToken("invalid token");

        // then
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Test find id refresh token by user username functionality")
    public void givenUsername_whenFindTokenIdByUserUsername_thenIdIsFound() {
        // given
        RefreshToken expected = EntityTestData.getTransientValidRefreshToken();
        entityManager.persist(expected.getUser());
        entityManager.persist(expected);

        // when
        Long actual = repository.findTokenIdByUserUsername(expected.getUser().getUsername());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected.getId());
    }

    @Test
    @DisplayName("Test find id refresh token by incorrect user username functionality")
    public void givenIncorrectUsername_whenFindTokenIdByUserUsername_thenIdIsNotFound() {
        // when
        Long actual = repository.findTokenIdByUserUsername("invalid username");

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("Test find user username by token functionality")
    public void givenToken_whenFindUsernameByToken_thenUsernameIsFound() {
        // given
        RefreshToken expected = EntityTestData.getTransientValidRefreshToken();
        entityManager.persist(expected.getUser());
        entityManager.persist(expected);

        // when
        String actual = repository.findUsernameByToken(expected.getToken());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected.getUser().getUsername());
    }

    @Test
    @DisplayName("Test find user username by incorrect token functionality")
    public void givenIncorrectToken_whenFindUsernameByToken_thenUsernameIsNotFound() {
        // when
        String actual = repository.findUsernameByToken("invalid token");

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("Test delete refresh token by token functionality")
    public void givenToken_whenDeleteByToken_thenTokenIsDeleted() {
        // given
        RefreshToken expected = EntityTestData.getTransientValidRefreshToken();
        entityManager.persist(expected.getUser());
        entityManager.persist(expected);

        // when
        repository.deleteByToken(expected.getToken());

        // then
        Optional<RefreshToken> actual = repository.findByToken(expected.getToken());
        assertThat(actual.isEmpty()).isTrue();
    }
}