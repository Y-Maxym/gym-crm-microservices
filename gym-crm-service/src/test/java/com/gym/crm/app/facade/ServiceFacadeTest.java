package com.gym.crm.app.facade;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.entity.TrainingType;
import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.AuthenticationException;
import com.gym.crm.app.exception.EntityPersistException;
import com.gym.crm.app.facade.mapper.AddTrainingMapper;
import com.gym.crm.app.facade.mapper.CreateTraineeProfileMapper;
import com.gym.crm.app.facade.mapper.CreateTrainerProfileMapper;
import com.gym.crm.app.facade.mapper.GetTraineeProfileMapper;
import com.gym.crm.app.facade.mapper.GetTraineeTrainingsMapper;
import com.gym.crm.app.facade.mapper.GetTrainerProfileMapper;
import com.gym.crm.app.facade.mapper.GetTrainerTrainingsMapper;
import com.gym.crm.app.facade.mapper.TrainerProfileMapper;
import com.gym.crm.app.facade.mapper.TrainingTypeMapper;
import com.gym.crm.app.facade.mapper.UpdateTraineeProfileMapper;
import com.gym.crm.app.facade.mapper.UpdateTrainerProfileMapper;
import com.gym.crm.app.rest.model.ActivateDeactivateProfileRequest;
import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.rest.model.ChangePasswordRequest;
import com.gym.crm.app.rest.model.TraineeCreateRequest;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.rest.model.TrainerProfileOnlyUsername;
import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import com.gym.crm.app.rest.model.UpdateTrainerProfileRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.security.AuthService;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.TrainingTypeService;
import com.gym.crm.app.service.UserService;
import com.gym.crm.app.service.common.BindingResultsService;
import com.gym.crm.app.service.common.UserProfileService;
import com.gym.crm.app.service.search.TraineeTrainingSearchFilter;
import com.gym.crm.app.service.search.TrainerTrainingSearchFilter;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

import static com.gym.crm.app.rest.exception.ErrorCode.PASSWORD_CHANGE_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINEE_CREATE_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINER_CREATE_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private UserService userService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AuthService authService;

    @Mock
    private BindingResultsService bindingResultsService;

    @Mock
    private TrainerProfileMapper trainerProfileMapper;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private CreateTraineeProfileMapper createTraineeProfileMapper;

    @Mock
    private CreateTrainerProfileMapper createTrainerProfileMapper;

    @Mock
    private GetTraineeProfileMapper getTraineeProfileMapper;

    @Mock
    private GetTrainerProfileMapper getTrainerProfileMapper;

    @Mock
    private UpdateTrainerProfileMapper updateTrainerProfileMapper;

    @Mock
    private UpdateTraineeProfileMapper updateTraineeProfileMapper;

    @Mock
    private GetTrainerTrainingsMapper getTrainerTrainingsMapper;

    @Mock
    private GetTraineeTrainingsMapper getTraineeTrainingsMapper;

    @Mock
    private AddTrainingMapper addTrainingMapper;

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private ServiceFacade serviceFacade;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Test create trainer profile by valid data functionality")
    void givenValidCreateTrainerDto_whenCreateTrainerProfile_thenCreateTrainerProfile() {
        // given
        TrainerCreateRequest request = EntityTestData.getValidCreateTrainerProfileRequest();
        Trainer trainer = EntityTestData.getPersistedTrainerEmilyDavis();
        User user = EntityTestData.getTransientUserEmilyDavis();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTrainerProfile");

        String errorMessage = "Trainer creation error";
        String password = "password";

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), eq(errorMessage), eq(TRAINER_CREATE_ERROR.getCode()));

        given(createTrainerProfileMapper.mapToTrainer(request))
                .willReturn(trainer);
        given(userProfileService.generatePassword())
                .willReturn(password);
        given(userService.prepareUserForSave(any(User.class), eq(password)))
                .willReturn(user);

        // when
        serviceFacade.createTrainerProfile(request, bindingResult);

        // then
        verify(trainerService).save(any(Trainer.class));
        verify(createTrainerProfileMapper).mapToUserCredentials(any(Trainer.class));
    }

    @Test
    @DisplayName("Test create trainer profile by invalid data functionality")
    void givenInvalidCreateTrainerDto_whenCreateTrainerProfile_thenExceptionIsThrown() {
        // given
        TrainerCreateRequest request = EntityTestData.getInvalidCreateTrainerProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTrainerProfile");

        doThrow(EntityPersistException.class).when(bindingResultsService).handle(any(), any(), any(), any());

        // when & then
        assertThrows(EntityPersistException.class, () -> serviceFacade.createTrainerProfile(request, bindingResult));
    }

    @Test
    @DisplayName("Test create trainee profile by valid data functionality")
    void givenValidCreateTraineeDto_whenCreateTraineeProfile_thenCreateTraineeProfile() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();
        User user = EntityTestData.getPersistedUserJohnDoe();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTraineeProfile");

        String errorMessage = "Trainee creation error";
        String password = "password";

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), eq(errorMessage), eq(TRAINEE_CREATE_ERROR.getCode()));

        given(createTraineeProfileMapper.mapToTrainee(request))
                .willReturn(trainee);
        given(userProfileService.generatePassword())
                .willReturn(password);
        given(userService.prepareUserForSave(any(User.class), eq(password)))
                .willReturn(user);

        // when
        serviceFacade.createTraineeProfile(request, bindingResult);

        // then
        verify(traineeService).save(any(Trainee.class));
        verify(createTraineeProfileMapper).mapToUserCredentials(any(Trainee.class));
    }

    @Test
    @DisplayName("Test create trainee profile by invalid data functionality")
    void givenInvalidCreateTraineeDto_whenCreateTraineeProfile_thenExceptionIsThrown() {
        // given
        TraineeCreateRequest request = EntityTestData.getInvalidCreateTraineeProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTraineeProfile");

        doThrow(EntityPersistException.class).when(bindingResultsService).handle(any(), any(), any(), any());

        // when & then
        assertThrows(EntityPersistException.class, () -> serviceFacade.createTraineeProfile(request, bindingResult));
    }

    @Test
    @DisplayName("Test find trainer profile by valid credentials functionality")
    void givenValidCredentials_whenFindTrainerProfile_thenFindTrainerProfile() {
        // given
        String username = "Emily.Davis";
        User user = EntityTestData.getPersistedUserEmilyDavis();
        Trainer trainer = EntityTestData.getPersistedTrainerEmilyDavis();

        given(trainerService.findByUsername(username))
                .willReturn(trainer);

        // when
        serviceFacade.findTrainerProfileByUsername(username);

        // then
        verify(trainerService).findByUsername(user.getUsername());
        verify(getTrainerProfileMapper).mapToGetTrainerProfileResponse(trainer);
    }

    @Test
    @DisplayName("Test find trainee profile by valid credentials functionality")
    void givenValidCredentials_whenFindTraineeProfile_thenFindTraineeProfile() {
        // given
        String username = "John.Doe";
        User user = EntityTestData.getPersistedUserJohnDoe();
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();

        given(traineeService.findByUsername(username))
                .willReturn(trainee);

        // when
        serviceFacade.findTraineeProfileByUsername(username);

        // then
        verify(traineeService).findByUsername(user.getUsername());
        verify(getTraineeProfileMapper).mapToGetTraineeProfileResponse(trainee);
    }

    @Test
    @DisplayName("Test change password functionality")
    void givenValidCredentials_whenChangePassword_thenChangePassword() {
        // given
        User user = EntityTestData.getPersistedUserEmilyDavis();
        ChangePasswordRequest request = EntityTestData.getValidChangePasswordRequest();
        String newPassword = request.getNewPassword();
        String hashedPassword = "hashedPassword";
        String errorMessage = "Password change error";

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "changePassword");

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), eq(errorMessage), eq(PASSWORD_CHANGE_ERROR.getCode()));

        given(userService.findByUsername(request.getUsername()))
                .willReturn(user);
        given(userProfileService.hashPassword(newPassword))
                .willReturn(hashedPassword);
        given(userProfileService.isPasswordCorrect(request.getPassword(), user.getPassword()))
                .willReturn(true);

        // when
        serviceFacade.changePassword(request, bindingResult, user);

        // then
        verify(userService).update(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(capturedUser.getPassword()).isEqualTo(hashedPassword);
    }

    @Test
    @DisplayName("Test update trainer profile by valid data functionality")
    void givenValidTrainerDto_whenUpdateTrainerProfile_thenUpdateTrainerProfile() {
        // given
        UpdateTrainerProfileRequest request = EntityTestData.getValidUpdateTrainerProfileRequest();
        User user = EntityTestData.getPersistedUserEmilyDavis();
        String username = user.getUsername();
        Trainer trainer = EntityTestData.getPersistedTrainerEmilyDavis();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTrainerProfile");

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), any(), any());

        given(trainerService.findByUsername(any()))
                .willReturn(trainer);
        given(updateTrainerProfileMapper.updateTraineeProfileFromDto(request, trainer))
                .willReturn(trainer);

        // when
        serviceFacade.updateTrainerProfile(username, request, bindingResult, user);

        // then
        verify(trainerService).update(any(Trainer.class));
        verify(updateTrainerProfileMapper).mapToUpdateTrainerProfileResponse(any());
    }

    @Test
    @DisplayName("Test update trainer profile by invalid data functionality")
    void givenInvalidTrainerDto_whenUpdateTrainerProfile_thenExceptionIsThrown() {
        // given
        UpdateTrainerProfileRequest request = EntityTestData.getInvalidTrainerProfileRequest();
        String username = "invalidUsername";
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTrainerProfile");

        User user = EntityTestData.getPersistedUserEmilyDavis();

        doThrow(EntityPersistException.class).when(bindingResultsService).handle(any(), any(), any(), any());

        // when & then
        assertThrows(EntityPersistException.class, () -> serviceFacade.updateTrainerProfile(username, request, bindingResult, user));
    }

    @Test
    @DisplayName("Test update trainee profile by valid data functionality")
    void givenValidTraineeDto_whenUpdateTraineeProfile_thenUpdateTrainerProfile() {
        // given
        UpdateTraineeProfileRequest request = EntityTestData.getValidTraineeProfileRequest();
        User user = EntityTestData.getPersistedUserJohnDoe();
        String username = user.getUsername();
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTraineeProfile");


        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), any(), any());

        given(updateTraineeProfileMapper.updateTraineeProfileFromDto(request, trainee))
                .willReturn(trainee);
        given(traineeService.findByUsername(any()))
                .willReturn(trainee);

        // when
        serviceFacade.updateTraineeProfile(username, request, bindingResult, user);

        // then
        verify(traineeService).update(any(Trainee.class));
        verify(updateTraineeProfileMapper).mapToUpdateTraineeProfileResponse(any());
    }

    @Test
    @DisplayName("Test update trainee profile by invalid data functionality")
    void givenInvalidTraineeDto_whenUpdateTraineeProfile_thenExceptionIsThrown() {
        // given
        String username = "invalidUsername";
        UpdateTraineeProfileRequest request = EntityTestData.getInvalidTraineeProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTraineeProfile");
        User user = EntityTestData.getPersistedUserJohnDoe();

        doThrow(EntityPersistException.class).when(bindingResultsService).handle(any(), any(), any(), any());

        // when & then
        assertThrows(EntityPersistException.class, () -> serviceFacade.updateTraineeProfile(username, request, bindingResult, user));
    }

    @Test
    @DisplayName("Test activate profile when profile is activated functionality")
    void givenActivatedProfile_whenActivateProfile_thenActivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe().toBuilder().isActive(true).build();

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), any(), any());
        given(userService.findByUsername(username))
                .willReturn(user);

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        verify(userService, never()).update(user);
    }

    @Test
    @DisplayName("Test activate profile when profile is deactivated functionality")
    void givenDeactivatedProfile_whenActivateProfile_thenActivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe().toBuilder().isActive(false).build();

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), any(), any());
        given(userService.findByUsername(username))
                .willReturn(user);

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        verify(userService).update(any(User.class));
    }

    @Test
    @DisplayName("Test deactivate profile when profile is deactivated functionality")
    void givenDeactivatedProfile_whenDeactivateProfile_thenDeactivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getDeactivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe().toBuilder().isActive(false).build();

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), any(), any());
        given(userService.findByUsername(username))
                .willReturn(user);

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        verify(userService, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Test deactivate profile when profile is activated functionality")
    void givenActivatedProfile_whenDeactivateProfile_thenDeactivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getDeactivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe().toBuilder().isActive(true).build();

        doNothing().when(bindingResultsService).handle(eq(bindingResult), any(), any(), any());
        given(userService.findByUsername(username))
                .willReturn(user);

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        verify(userService).update(any(User.class));
    }

    @Test
    @DisplayName("Test delete trainee by username functionality")
    void whenDeleteTraineeByUsername_thenDeleteTraineeByUsername() {
        // given
        String username = "John.Doe";
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when
        serviceFacade.deleteTraineeProfileByUsername(username, user);

        // then
        verify(traineeService).deleteByUsername(user.getUsername());
    }

    @Test
    @DisplayName("Test get trainee trainings by criteria functionality")
    void givenCriteria_whenGetTraineeTrainings_thenTrainingsIsReturned() {
        // given
        String username = "John.Doe";

        LocalDate from = LocalDate.parse("2020-01-01");
        LocalDate to = LocalDate.parse("2020-01-02");
        String trainerName = "Emily";

        // when
        serviceFacade.getTraineeTrainingsByCriteria(username, from, to, trainerName, null);

        // then
        verify(traineeService).findTrainingByCriteria(any(TraineeTrainingSearchFilter.class));
    }

    @Test
    @DisplayName("Test get trainer trainings by criteria functionality")
    void givenCriteria_whenGetTrainerTrainings_thenTrainingsIsReturned() {
        // given
        String username = "David.Brown";

        LocalDate from = LocalDate.parse("2020-01-01");
        LocalDate to = LocalDate.parse("2020-01-02");
        String traineeName = "John";

        // when
        serviceFacade.getTrainerTrainingsByCriteria(username, from, to, traineeName);

        // then
        verify(trainerService).findTrainingByCriteria(any(TrainerTrainingSearchFilter.class));
    }

    @Test
    @DisplayName("Test add valid training functionality")
    void givenTrainingRequest_whenAddTraining_thenTrainingIsAdded() {
        // given
        AddTrainingRequest request = EntityTestData.getValidTrainingRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "addTraining");
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();
        Trainer trainer = EntityTestData.getPersistedTrainerDavidBrown();
        Training training = EntityTestData.getPersistedTrainingDavidBrown();

        doNothing().when(bindingResultsService).handle(any(), any(), any(), any());

        given(traineeService.findByUsername(request.getTraineeUsername()))
                .willReturn(trainee);
        given(trainerService.findByUsername(request.getTrainerUsername()))
                .willReturn(trainer);
        given(addTrainingMapper.mapToTraining(request))
                .willReturn(training);

        // when
        serviceFacade.addTraining(request, bindingResult);

        // then
        verify(trainingService).save(any());
        verify(addTrainingMapper).mapToTraining(request);
    }

    @Test
    @DisplayName("Test add invalid training functionality")
    void givenInvalidTrainingRequest_whenAddTraining_thenExceptionIsThrown() {
        // given
        AddTrainingRequest request = EntityTestData.getInvalidTrainingRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "addTraining");

        doThrow(EntityPersistException.class).when(bindingResultsService).handle(any(), any(), any(), any());

        // when & then
        assertThrows(EntityPersistException.class, () -> serviceFacade.addTraining(request, bindingResult));
    }

    @Test
    @DisplayName("Test get trainer not assigned by trainee username functionality")
    void givenUsername_whenGetTrainerNotAssigned_thenTrainersIsReturned() {
        // given
        String username = "John.Doe";

        // when
        serviceFacade.getTrainersNotAssignedByTraineeUsername(username);

        // then
        verify(trainerService).getTrainersNotAssignedByTraineeUsername(username);
    }

    @Test
    @DisplayName("Test add trainer to trainee functionality")
    void givenTrainerUsername_whenAddTrainerToTrainee_thenTrainerIsAdded() {
        // given
        String username = "John.Doe";
        User user = EntityTestData.getPersistedUserJohnDoe();
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();
        String trainerUsername = "David.Brown";

        List<TrainerProfileOnlyUsername> trainers = EntityTestData.getValidListTrainerProfileOnlyUsernames();

        given(traineeService.findByUsername(username))
                .willReturn(trainee);

        // when
        serviceFacade.updateTraineesTrainerList(username, trainers, user);

        // then
        verify(trainerService, atLeastOnce()).findByUsername(trainerUsername);
        verify(traineeService).update(trainee);
    }

    @Test
    @DisplayName("Test get graining type list functionality")
    void givenTrainingTypeList_whenGetTrainingTypeList_thenTrainingTypeListIsReturned() {
        // given
        List<TrainingType> trainingTypes = List.of(EntityTestData.getTrainingTypeFitness(), EntityTestData.getTrainingTypeYoga());

        given(trainingTypeService.findAll())
                .willReturn(trainingTypes);

        // when
        serviceFacade.getTrainingTypes();

        // then
        verify(trainingTypeService).findAll();
        verify(trainingTypeMapper, times(trainingTypes.size())).mapToGetTrainingTypeResponse(any(TrainingType.class));
    }

    @Test
    @DisplayName("Test authenticate functionality")
    void givenUserCredentials_whenAuthenticate_thenAuthenticateIsReturned() {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        BindingResult bindingResult = new BeanPropertyBindingResult(credentials, "userCredentials");

        doNothing().when(bindingResultsService).handle(any(), any(), any(), any());

        // when
        serviceFacade.authenticate(credentials, bindingResult);

        // then
        verify(bindingResultsService).handle(any(), any(), any(), any());
        verify(authService).authenticate(credentials.getUsername(), credentials.getPassword());
    }

    @Test
    @DisplayName("Test check valid username password functionality")
    void givenUsernamePassword_whenCheckCredentials_thenExceptionIsNotThrown() {
        // given
        String username = "John.Doe";
        String password = "password";
        User user = EntityTestData.getPersistedUserJohnDoe();

        given(userProfileService.isPasswordCorrect(password, user.getPassword()))
                .willReturn(true);

        // when && then
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(serviceFacade, "checkCredentials", username, password, user));
    }

    @Test
    @DisplayName("Test check invalid username functionality")
    void givenInvalidUsername_whenCheckCredentials_thenExceptionIsThrown() {
        // given
        String username = "Emily.Davis";
        String password = "password";
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when && then
        assertThrows(AuthenticationException.class, () -> ReflectionTestUtils.invokeMethod(serviceFacade, "checkCredentials", username, password, user));
    }

    @Test
    @DisplayName("Test check invalid password functionality")
    void givenInvalidPassword_whenCheckCredentials_thenExceptionIsThrown() {
        // given
        String username = "John.Doe";
        String password = "password";
        User user = EntityTestData.getPersistedUserJohnDoe();

        given(userProfileService.isPasswordCorrect(password, user.getPassword()))
                .willReturn(false);

        // when && then
        assertThrows(AuthenticationException.class, () -> ReflectionTestUtils.invokeMethod(serviceFacade, "checkCredentials", username, password, user));
    }

    @Test
    @DisplayName("Test check valid username password functionality")
    void givenUsername_whenCheckUsername_thenExceptionIsNotThrown() {
        // given
        String username = "John.Doe";
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when && then
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(serviceFacade, "checkUsername", username, user));
    }

    @Test
    @DisplayName("Test check invalid username functionality")
    void givenInvalidUsername_whenCheckUsername_thenExceptionIsThrown() {
        // given
        String username = "Emily.Davis";
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when && then
        assertThrows(AuthenticationException.class, () -> ReflectionTestUtils.invokeMethod(serviceFacade, "checkUsername", username, user));
    }
}