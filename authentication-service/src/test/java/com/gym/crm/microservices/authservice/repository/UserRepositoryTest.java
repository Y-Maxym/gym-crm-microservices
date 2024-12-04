package com.gym.crm.microservices.authservice.repository;

import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class UserRepositoryTest extends AbstractTestRepository<UserRepository> {

    @Test
    @DisplayName("Test find user by id functionality")
    public void givenId_whenFindById_thenUserIsReturned() {
        // given
        User expected = EntityTestData.getTransientUserJohnDoe();
        entityManager.persist(expected);

        // when
        Optional<User> actual = repository.findById(expected.getId());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find user by incorrect id functionality")
    public void givenIncorrectId_whenFindById_thenNullIsReturned() {
        // given
        User expected = EntityTestData.getPersistedUserJohnDoe();

        // when
        Optional<User> actual = repository.findById(expected.getId());

        // then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Test find all user functionality")
    public void givenUsers_whenFindAll_thenUsersIsReturned() {
        // given
        User user1 = EntityTestData.getTransientUserJohnDoe();
        User user2 = EntityTestData.getTransientUserJaneSmith();
        User user3 = EntityTestData.getTransientUserMichaelJohnson();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // when
        List<User> actual = repository.findAll();

        // then
        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual).containsAll(List.of(user1, user2, user3));
    }

    @Test
    @DisplayName("Test save user without id functionality")
    public void givenUser_whenSaveUser_thenUserIsSaved() {
        // given
        User user = EntityTestData.getTransientUserJohnDoe();

        // when
        User actual = repository.save(user);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test delete user by id functionality")
    public void givenId_whenDeleteById_thenUserIsDeleted() {
        // given
        User user = EntityTestData.getTransientUserJohnDoe();
        entityManager.persist(user);
        entityManager.clear();

        // when
        repository.deleteById(user.getId());

        // then
        User actual = entityManager.find(User.class, user.getId());

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("Test find user by username functionality")
    public void givenUsername_whenFindByUsername_thenUserIsFound() {
        // given
        User expected = EntityTestData.getTransientUserJohnDoe();
        entityManager.persist(expected);

        // when
        Optional<User> actual = repository.findByUsername(expected.getUsername());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find user by incorrect username functionality")
    public void givenIncorrectUsername_whenFindByUsername_thenUserIsNotFound() {
        // given
        User expected = EntityTestData.getTransientUserJohnDoe();
        entityManager.persist(expected);

        // when
        Optional<User> actual = repository.findByUsername("username");

        // then
        assertThat(actual.isEmpty()).isTrue();
    }
}
