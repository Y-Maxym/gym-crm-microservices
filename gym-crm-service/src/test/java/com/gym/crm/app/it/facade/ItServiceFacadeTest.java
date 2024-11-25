package com.gym.crm.app.it.facade;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.AuthenticationException;
import com.gym.crm.app.exception.EntityPersistException;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.facade.ServiceFacade;
import com.gym.crm.app.it.AbstractItTest;
import com.gym.crm.app.rest.model.ActivateDeactivateProfileRequest;
import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.rest.model.ChangePasswordRequest;
import com.gym.crm.app.rest.model.GetTraineeProfileResponse;
import com.gym.crm.app.rest.model.GetTraineeTrainingsResponse;
import com.gym.crm.app.rest.model.GetTrainerProfileResponse;
import com.gym.crm.app.rest.model.GetTrainerTrainingsResponse;
import com.gym.crm.app.rest.model.GetTrainingTypeResponse;
import com.gym.crm.app.rest.model.TraineeCreateRequest;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.rest.model.TrainerProfileOnlyUsername;
import com.gym.crm.app.rest.model.TrainerProfileWithUsername;
import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import com.gym.crm.app.rest.model.UpdateTrainerProfileRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.service.common.UserProfileService;
import com.gym.crm.app.utils.EntityTestData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Transactional
@Rollback
class ItServiceFacadeTest extends AbstractItTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ServiceFacade serviceFacade;

    @Test
    @DisplayName("Test create trainer profile by valid data functionality")
    void givenValidCreateTrainerDto_whenCreateTrainerProfile_thenCreateTrainerProfile() {
        // given
        TrainerCreateRequest request = EntityTestData.getValidCreateTrainerProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTrainerProfile");

        // when
        UserCredentials actual = serviceFacade.createTrainerProfile(request, bindingResult);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isNotNull();
        assertThat(actual.getPassword()).isNotNull();
    }

    @Test
    @DisplayName("Test create trainer profile by invalid data functionality")
    void givenInvalidCreateTrainerDto_whenCreateTrainerProfile_thenExceptionIsThrown() {
        // given
        TrainerCreateRequest request = EntityTestData.getInvalidCreateTrainerProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTrainerProfile");

        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");

        // when
        EntityPersistException ex = assertThrows(EntityPersistException.class, () -> serviceFacade.createTrainerProfile(request, bindingResult));

        // then
        assertThat(ex.getMessage()).isEqualTo("Trainer creation error");
    }

    @Test
    @DisplayName("Test create trainee profile by valid data functionality")
    void givenValidCreateTraineeDto_whenCreateTraineeProfile_thenCreateTraineeProfile() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTrainerProfile");

        // when
        UserCredentials actual = serviceFacade.createTraineeProfile(request, bindingResult);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isNotNull();
        assertThat(actual.getPassword()).isNotNull();
    }

    @Test
    @DisplayName("Test create trainee profile by invalid data functionality")
    void givenInvalidCreateTraineeDto_whenCreateTraineeProfile_thenExceptionIsThrown() {
        // given
        TraineeCreateRequest request = EntityTestData.getInvalidCreateTraineeProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "createTrainerProfile");

        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");

        // when
        EntityPersistException ex = assertThrows(EntityPersistException.class, () -> serviceFacade.createTraineeProfile(request, bindingResult));

        // then
        assertThat(ex.getMessage()).isEqualTo("Trainee creation error");
    }

    @Test
    @DisplayName("Test find trainer profile by valid username functionality")
    void givenValidCredentials_whenFindTrainerProfile_thenFindTrainerProfile() {
        // given
        String username = "David.Brown";

        // when
        GetTrainerProfileResponse actual = serviceFacade.findTrainerProfileByUsername(username);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo("David");
        assertThat(actual.getLastName()).isEqualTo("Brown");
    }

    @Test
    @DisplayName("Test find trainer profile by invalid username functionality")
    void givenTraineeCredentials_whenFindTrainerProfile_thenTrainerProfileNotFound() {
        // given
        String username = "invalid";

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> serviceFacade.findTrainerProfileByUsername(username));

        // then
        assertThat(ex.getMessage()).isEqualTo("Trainer with username %s not found".formatted(username));
    }

    @Test
    @DisplayName("Test find trainee profile by valid username functionality")
    void givenValidCredentials_whenFindTraineeProfile_thenFindTraineeProfile() {
        // given
        String username = "John.Doe";

        // when
        GetTraineeProfileResponse actual = serviceFacade.findTraineeProfileByUsername(username);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo("John");
        assertThat(actual.getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Test find trainee profile by invalid username functionality")
    void givenTrainerCredentials_whenFindTraineeProfile_thenTrainerProfileNotFound() {
        // given
        String username = "invalid";

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> serviceFacade.findTraineeProfileByUsername(username));

        // then
        assertThat(ex.getMessage()).isEqualTo("Trainee with username %s not found".formatted(username));
    }

    @Test
    @DisplayName("Test change password functionality")
    void givenValidCredentials_whenChangePassword_thenChangePassword() {
        // given
        ChangePasswordRequest request = EntityTestData.getValidChangePasswordRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "changePasswordRequest");
        User user = EntityTestData.getPersistedUserEmilyDavis();

        // when
        serviceFacade.changePassword(request, bindingResult, user);

        // then
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", request.getUsername())
                .getSingleResult();

        assertTrue(userProfileService.isPasswordCorrect(request.getNewPassword(), actual.getPassword()));
    }

    @Test
    @DisplayName("Test change password by invalid user functionality")
    void givenInvalidUser_whenChangePassword_thenExceptionIsThrown() {
        // given
        ChangePasswordRequest request = EntityTestData.getValidChangePasswordRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "changePasswordRequest");
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when
        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> serviceFacade.changePassword(request, bindingResult, user));

        // then
        assertThat(ex.getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("Test change password by invalid password functionality")
    void givenInvalidPassword_whenChangePassword_thenExceptionIsThrown() {
        // given
        ChangePasswordRequest request = EntityTestData.getInvalidChangePasswordRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "changePasswordRequest");
        User user = EntityTestData.getTransientUserEmilyDavis();

        // when
        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> serviceFacade.changePassword(request, bindingResult, user));

        // then
        assertThat(ex.getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("Test update trainer profile by valid data functionality")
    void givenValidTrainerDto_whenUpdateTrainerProfile_thenUpdateTrainerProfile() {
        // given
        String username = "Emily.Davis";
        UpdateTrainerProfileRequest request = EntityTestData.getValidUpdateTrainerProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTrainerProfile");
        User user = EntityTestData.getPersistedUserEmilyDavis();

        // when
        serviceFacade.updateTrainerProfile(username, request, bindingResult, user);

        // then
        User actual = entityManager.createQuery("FROM User u WHERE u.firstName = :firstName", User.class)
                .setParameter("firstName", request.getFirstName())
                .getSingleResult();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Test update trainer profile by invalid data functionality")
    void givenInvalidTrainerDto_whenUpdateTrainerProfile_thenExceptionIsThrown() {
        // given
        String username = "Emily.Davis";
        UpdateTrainerProfileRequest request = EntityTestData.getInvalidTrainerProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTrainerProfile");

        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");

        User user = EntityTestData.getPersistedUserEmilyDavis();

        // when
        EntityPersistException ex = assertThrows(EntityPersistException.class, () -> serviceFacade.updateTrainerProfile(username, request, bindingResult, user));

        // then
        assertThat(ex.getMessage()).isEqualTo("Trainer update error");
    }

    @Test
    @DisplayName("Test update trainee profile by valid data functionality")
    void givenValidTraineeDto_whenUpdateTraineeProfile_thenUpdateTrainerProfile() {
        // given
        String username = "John.Doe";
        UpdateTraineeProfileRequest request = EntityTestData.getValidTraineeProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTraineeProfile");
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when
        serviceFacade.updateTraineeProfile(username, request, bindingResult, user);

        // then
        User actual = entityManager.createQuery("FROM User u WHERE u.firstName = :firstName", User.class)
                .setParameter("firstName", request.getFirstName())
                .getSingleResult();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Test update trainee profile by invalid data functionality")
    void givenInvalidTraineeDto_whenUpdateTraineeProfile_thenExceptionIsThrown() {
        // given
        String username = "John.Doe";
        UpdateTraineeProfileRequest request = EntityTestData.getInvalidTraineeProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "updateTrainerProfile");

        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");

        User user = EntityTestData.getPersistedUserEmilyDavis();

        // when
        EntityPersistException ex = assertThrows(EntityPersistException.class, () -> serviceFacade.updateTraineeProfile(username, request, bindingResult, user));

        // then
        assertThat(ex.getMessage()).isEqualTo("Trainee update error");
    }

    @Test
    @DisplayName("Test activate profile when profile is activated functionality")
    void givenActivatedProfile_whenActivateProfile_thenActivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertTrue(actual.isActive());
    }

    @Test
    @DisplayName("Test activate profile when profile is deactivated functionality")
    void givenDeactivatedProfile_whenActivateProfile_thenActivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe();

        entityManager.createQuery("UPDATE User u SET u.isActive = false WHERE u.username = :username")
                .setParameter("username", username)
                .executeUpdate();

        boolean beforeActivate = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult()
                .isActive();

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertFalse(beforeActivate);
        assertTrue(actual.isActive());
    }

    @Test
    @DisplayName("Test deactivate profile when profile is deactivated functionality")
    void givenDeactivatedProfile_whenDeactivateProfile_thenDeactivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getDeactivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe();

        entityManager.createQuery("UPDATE User u SET u.isActive = false WHERE u.username = :username")
                .setParameter("username", username)
                .executeUpdate();

        boolean beforeActivate = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult()
                .isActive();

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertFalse(beforeActivate);
        assertFalse(actual.isActive());
    }

    @Test
    @DisplayName("Test deactivate profile when profile is activated functionality")
    void givenActivatedProfile_whenDeactivateProfile_thenDeactivateProfile() {
        // given
        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getDeactivateProfileRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when
        serviceFacade.activateDeactivateProfile(username, request, bindingResult, user);

        // then
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertFalse(actual.isActive());
    }

    @Test
    @DisplayName("Test delete trainee by username functionality")
    void whenDeleteTraineeByUsername_thenDeleteTraineeByUsername() {
        // given
        String username = "John.Doe";
        User user = EntityTestData.getPersistedUserJohnDoe();

        // when
        List<Trainee> beforeDelete = entityManager.createQuery("FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultList();

        serviceFacade.deleteTraineeProfileByUsername(username, user);

        // then
        List<Trainee> actual = entityManager.createQuery("FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultList();

        assertThat(beforeDelete).isNotEmpty();
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("Test delete trainee by incorrect username functionality")
    void givenIncorrectUser_whenDeleteTraineeByUsername_thenDeleteTraineeByUsername() {
        // given
        String username = "John.Doe";
        User user = EntityTestData.getPersistedUserDavidBrown();

        // when
        List<Trainee> beforeDelete = entityManager.createQuery("FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultList();

        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> serviceFacade.deleteTraineeProfileByUsername(username, user));

        // then
        assertThat(beforeDelete).isNotEmpty();
        assertThat(ex.getMessage()).isEqualTo("Invalid username");
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
        List<GetTraineeTrainingsResponse> actual = serviceFacade.getTraineeTrainingsByCriteria(username, from, to, trainerName, null);

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("Test get trainer trainings by criteria functionality")
    void givenCriteria_whenGetTrainerTrainings_thenTrainingsIsReturned() {
        // given
        String username = "Emily.Davis";

        LocalDate from = LocalDate.parse("2020-01-01");
        LocalDate to = LocalDate.parse("2020-01-02");
        String traineeName = "John";

        // when
        List<GetTrainerTrainingsResponse> actual = serviceFacade.getTrainerTrainingsByCriteria(username, from, to, traineeName);

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("Test add training functionality")
    void givenTrainingRequest_whenAddTraining_thenTrainingIsAdded() {
        // given
        AddTrainingRequest request = EntityTestData.getValidTrainingRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "addTraining");

        // when
        serviceFacade.addTraining(request, bindingResult);

        // then
        Training actual = entityManager.createQuery("FROM Training t WHERE t.trainingName = :trainingName", Training.class)
                .setParameter("trainingName", request.getTrainingName())
                .getSingleResult();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Test add training functionality")
    void givenInvalidTrainingRequest_whenAddTraining_thenExceptionIsThrown() {
        // given
        AddTrainingRequest request = EntityTestData.getInvalidTrainingRequest();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "addTraining");

        bindingResult.rejectValue("traineeUsername", "trainee.username");
        bindingResult.rejectValue("trainerUsername", "trainer.username");

        // when
        EntityPersistException ex = assertThrows(EntityPersistException.class, () -> serviceFacade.addTraining(request, bindingResult));

        // then
        assertThat(ex.getMessage()).isEqualTo("Training creation error");
    }

    @Test
    @DisplayName("Test get trainer not assigned by trainee username functionality")
    void givenUsername_whenGetTrainerNotAssigned_thenTrainersIsReturned() {
        // given
        String username = "John.Doe";

        // when
        List<TrainerProfileWithUsername> actual = serviceFacade.getTrainersNotAssignedByTraineeUsername(username);

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("Test add trainer to trainee functionality")
    void givenTrainerUsername_whenAddTrainerToTrainee_thenTrainerIsAdded() {
        // given
        String username = "John.Doe";
        List<TrainerProfileOnlyUsername> request = EntityTestData.getValidListTrainerProfileOnlyUsernames();
        User user = EntityTestData.getPersistedUserJohnDoe();

        List<Trainer> beforeAdd = entityManager.createQuery("SELECT t.trainers FROM Trainee t WHERE t.user.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultList();

        // when
        serviceFacade.updateTraineesTrainerList(username, request, user);

        // then
        List<Trainer> actual = entityManager.createQuery("SELECT t.trainers FROM Trainee t WHERE t.user.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultList();

        assertThat(beforeAdd).hasSize(1);
        assertThat(actual).hasSize(2);
    }

    @Test
    @DisplayName("Test get training types functionality")
    void whenGetTrainingTypes_thenGetTrainingTypesIsReturned() {
        // when
        List<GetTrainingTypeResponse> trainingTypes = serviceFacade.getTrainingTypes();

        // then
        assertThat(trainingTypes).hasSize(5);
    }

    @Test
    @DisplayName("Test authenticate functionality")
    void givenUserCredentials_whenAuthenticate_thenAuthenticateIsCorrect() {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        BindingResult bindingResult = new BeanPropertyBindingResult(credentials, "userCredentials");

        // when
        Authentication actual = serviceFacade.authenticate(credentials, bindingResult);

        // then
        assertThat(actual).isNotNull();
    }
}