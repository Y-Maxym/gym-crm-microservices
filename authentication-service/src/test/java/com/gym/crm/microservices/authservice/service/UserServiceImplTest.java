package com.gym.crm.microservices.authservice.service;

import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.exception.EntityValidationException;
import com.gym.crm.microservices.authservice.repository.UserRepository;
import com.gym.crm.microservices.authservice.service.common.EntityValidator;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    @DisplayName("Test find user by username functionality")
    public void givenUsername_whenFindByUsername_thenUserIsReturned() {
        // given
        User expected = EntityTestData.getPersistedUserJohnDoe();
        String username = expected.getUsername();

        given(repository.findByUsername(username))
                .willReturn(Optional.of(expected));

        // when
        User actual = service.findByUsername(username);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find user by incorrect username functionality")
    public void givenIncorrectUsername_whenFindByUsername_thenExceptionIsThrown() {
        // given
        String username = "username";
        String message = "User with username %s not found".formatted(username);

        given(repository.findByUsername(username))
                .willReturn(Optional.empty());

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.findByUsername(username));

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
