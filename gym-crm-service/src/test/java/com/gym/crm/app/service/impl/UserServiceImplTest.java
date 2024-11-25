package com.gym.crm.app.service.impl;

import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.service.common.EntityValidator;
import com.gym.crm.app.service.common.UserProfileService;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.gym.crm.app.util.Constants.ERROR_USER_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_USER_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.util.Constants.WARN_USER_WITH_ID_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    @DisplayName("Test find user by id functionality")
    public void givenId_whenFindById_thenUserIsReturned() {
        // given
        User expected = EntityTestData.getPersistedUserJohnDoe();
        long id = expected.getId();

        doNothing().when(entityValidator).checkId(id);
        given(repository.findById(id))
                .willReturn(Optional.of(expected));

        // when
        User actual = service.findById(id);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find user by incorrect id functionality")
    public void givenIncorrectId_whenFindById_thenExceptionIsThrown() {
        // given
        long id = 1L;
        String message = "User with id %s not found".formatted(id);

        doNothing().when(entityValidator).checkId(id);
        given(repository.findById(id))
                .willReturn(Optional.empty());
        given(messageHelper.getMessage(ERROR_USER_WITH_ID_NOT_FOUND, id))
                .willReturn(message);

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.findById(id));

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
    }

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
        given(messageHelper.getMessage(ERROR_USER_WITH_USERNAME_NOT_FOUND, username))
                .willReturn(message);

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.findByUsername(username));

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Test save user functionality")
    public void givenSaveUser_whenSave_thenRepositoryIsCalled() {
        // given
        User user = EntityTestData.getPersistedUserJohnDoe();
        String username = user.getFirstName() + "." + user.getLastName();
        String password = "1234567890";

        user = user.toBuilder().username(username).password(password).build();

        doNothing().when(entityValidator).checkEntity(user);
        given(userProfileService.generatePassword())
                .willReturn(password);
        given(repository.save(user))
                .willReturn(user);

        // when
        service.save(user);

        // then
        verify(userProfileService).generatePassword();
        verify(repository, only()).save(user);
    }

    @Test
    @DisplayName("Test prepare user without username and password functionality")
    public void givenSaveUserWithoutUsernamePassword_whenSave_thenRepositoryIsCalled() {
        // given
        User user = EntityTestData.getTransientUserJohnDoeWithoutData();

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = "%s.%s".formatted(firstName, lastName);
        String password = "password";

        given(userProfileService.hashPassword(password))
                .willReturn(password);
        given(userProfileService.generateUsername(firstName, lastName))
                .willReturn(username);

        // when
        User actual = ReflectionTestUtils.invokeMethod(service, "prepareUserForSave", user, password);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isEqualTo("%s.%s", firstName, lastName);
        assertThat(actual.getPassword()).isEqualTo(password);

        verify(userProfileService).generateUsername(user.getFirstName(), user.getLastName());
        verify(userProfileService).hashPassword(password);
    }

    @Test
    @DisplayName("Test prepare user with full data functionality")
    public void givenSaveUserWithFullData_whenPrepareUserForSave_thenUserIsReturned() {
        // given
        User persisted = EntityTestData.getPersistedUserJohnDoe();
        String username = persisted.getFirstName() + "." + persisted.getLastName();
        String password = "1234567890";

        User user = persisted.toBuilder().username(username).password(password).build();

        // when
        User actual = ReflectionTestUtils.invokeMethod(service, "prepareUserForSave", user, password);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Test update user functionality")
    public void givenUpdatedUser_whenUpdate_thenRepositoryIsCalled() {
        // given
        User user = EntityTestData.getPersistedUserJohnDoe();

        doNothing().when(entityValidator).checkEntity(user);
        doNothing().when(entityValidator).checkId(user.getId());

        // when
        service.update(user);

        // then
        verify(repository, only()).save(user);
    }

    @Test
    @DisplayName("Test delete user by id functionality")
    public void givenId_whenDeleteById_thenRepositoryIsCalled() {
        // given
        long id = 1L;

        doNothing().when(entityValidator).checkId(id);
        doNothing().when(repository).deleteById(id);
        given(repository.findById(id))
                .willReturn(Optional.of(EntityTestData.getPersistedUserJohnDoe()));

        // when
        service.deleteById(id);

        // then
        verify(messageHelper, never()).getMessage(WARN_USER_WITH_ID_NOT_FOUND, id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Test delete user by incorrect id functionality")
    public void givenIncorrectId_whenDeleteById_thenRepositoryIsCalled() {
        // given
        long id = 1L;

        doNothing().when(entityValidator).checkId(id);

        given(repository.findById(id))
                .willReturn(Optional.empty());

        // when
        service.deleteById(id);

        // then
        verify(messageHelper).getMessage(WARN_USER_WITH_ID_NOT_FOUND, id);
        verify(repository).deleteById(id);
    }
}
