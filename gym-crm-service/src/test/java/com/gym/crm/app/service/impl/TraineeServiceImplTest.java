package com.gym.crm.app.service.impl;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.EntityValidator;
import com.gym.crm.app.service.common.dto.TrainerSummaryRequest;
import com.gym.crm.app.service.search.TraineeTrainingSearchFilter;
import com.gym.crm.app.service.spectification.TraineeTrainingSpecification;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Optional;

import static com.gym.crm.app.util.Constants.ERROR_TRAINEE_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_TRAINEE_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.util.Constants.WARN_TRAINEE_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.WARN_TRAINEE_WITH_USERNAME_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private TraineeRepository repository;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TraineeServiceImpl service;

    @Test
    @DisplayName("Test find trainee by id functionality")
    public void givenId_whenFindById_thenTraineeIsReturned() {
        // given
        Trainee expected = EntityTestData.getPersistedTraineeJohnDoe();
        long id = expected.getId();

        doNothing().when(entityValidator).checkId(id);
        given(repository.findById(id))
                .willReturn(Optional.of(expected));

        // when
        Trainee actual = service.findById(id);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find trainee by incorrect id functionality")
    public void givenIncorrectId_whenFindById_thenExceptionIsThrown() {
        // given
        long id = 1L;
        String message = "Trainee with id %s not found".formatted(id);

        doNothing().when(entityValidator).checkId(id);
        given(repository.findById(id))
                .willReturn(Optional.empty());
        given(messageHelper.getMessage(ERROR_TRAINEE_WITH_ID_NOT_FOUND, id))
                .willReturn(message);

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.findById(id));

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Test find trainee by username functionality")
    public void givenUsername_whenFindByUsername_thenTraineeIsReturned() {
        // given
        Trainee expected = EntityTestData.getPersistedTraineeJohnDoe();
        String username = expected.getUser().getUsername();

        given(repository.findByUserUsername(username))
                .willReturn(Optional.of(expected));

        // when
        Trainee actual = service.findByUsername(username);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find trainee by incorrect username functionality")
    public void givenIncorrectUsername_whenFindByUsername_thenExceptionIsThrown() {
        // given
        String username = "username";
        String message = "Trainee with username %s not found".formatted(username);

        given(repository.findByUserUsername(username))
                .willReturn(Optional.empty());
        given(messageHelper.getMessage(ERROR_TRAINEE_WITH_USERNAME_NOT_FOUND, username))
                .willReturn(message);

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.findByUsername(username));

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Test save trainee functionality")
    public void givenSaveTrainee_whenSave_thenRepositoryIsCalled() {
        // given
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();

        doNothing().when(entityValidator).checkEntity(trainee);

        // when
        service.save(trainee);

        // then
        verify(repository, only()).save(trainee);
    }

    @Test
    @DisplayName("Test update trainee functionality")
    public void givenUpdatedTrainee_whenUpdate_thenRepositoryIsCalled() {
        // given
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();

        doNothing().when(entityValidator).checkEntity(trainee);
        doNothing().when(entityValidator).checkId(trainee.getId());

        // when
        service.update(trainee);

        // then
        verify(repository, only()).save(trainee);
    }

    @Test
    @DisplayName("Test delete trainee by id functionality")
    public void givenId_whenDeleteById_thenRepositoryIsCalled() {
        // given
        long id = 1L;

        doNothing().when(entityValidator).checkId(id);
        doNothing().when(repository).deleteById(id);
        given(repository.findById(id))
                .willReturn(Optional.of(EntityTestData.getPersistedTraineeJohnDoe()));

        // when
        service.deleteById(id);

        // then
        verify(messageHelper, never()).getMessage(WARN_TRAINEE_WITH_ID_NOT_FOUND, id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Test delete trainee by incorrect id functionality")
    public void givenIncorrectId_whenDeleteById_thenLogWarnIsCalled() {
        // given
        long id = 1L;

        doNothing().when(entityValidator).checkId(id);
        given(repository.findById(id))
                .willReturn(Optional.empty());

        // when
        service.deleteById(id);

        // then
        verify(messageHelper).getMessage(WARN_TRAINEE_WITH_ID_NOT_FOUND, id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Test delete trainee by username successfully")
    public void givenUsername_whenDeleteByUsername_thenRepositoryAndNotificationServiceAreCalled() {
        // given
        String username = EntityTestData.getPersistedTraineeJohnDoe().getUser().getUsername();
        Trainee trainee = EntityTestData.getPersistedTraineeJohnDoe();

        doNothing().when(entityValidator).checkEntity(username);
        given(repository.findByUserUsername(username)).willReturn(Optional.of(trainee));
        doNothing().when(repository).deleteByUserUsername(username);

        // when
        service.deleteByUsername(username);

        // then
        verify(repository).deleteByUserUsername(username);
        verify(trainingService, times(trainee.getTrainings().size()))
                .notifyTrainerSummaryService(any(Training.class), eq(TrainerSummaryRequest.ActionType.DELETE));
        verify(messageHelper, never()).getMessage(WARN_TRAINEE_WITH_USERNAME_NOT_FOUND, username);
    }

    @Test
    @DisplayName("Test delete trainee by non-existing username logs a warning")
    public void givenNonExistingUsername_whenDeleteByUsername_thenLogWarnIsCalled() {
        // given
        String username = "nonExistingUsername";

        doNothing().when(entityValidator).checkEntity(username);
        given(repository.findByUserUsername(username)).willReturn(Optional.empty());

        // when
        service.deleteByUsername(username);

        // then
        verify(repository, never()).deleteByUserUsername(username);
        verify(trainingService, never()).notifyTrainerSummaryService(any(), any());
        verify(messageHelper).getMessage(WARN_TRAINEE_WITH_USERNAME_NOT_FOUND, username);
    }

    @Test
    @DisplayName("Test find trainee trainings by criteria functionality")
    public void givenTraineeCriteria_whenFindTraineeTrainingByCriteria_thenRepositoryIsCalled() {
        // given
        String username = "username";
        LocalDate from = LocalDate.parse("2020-01-01");
        LocalDate to = LocalDate.parse("2020-01-01");
        String trainerName = "trainerName";
        String trainingType = "trainingType";

        TraineeTrainingSearchFilter searchFilter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .from(from)
                .to(to)
                .profileName(trainerName)
                .trainingType(trainingType)
                .build();
        Specification<Training> specification = TraineeTrainingSpecification.findByCriteria(searchFilter);

        MockedStatic<TraineeTrainingSpecification> mockedStatic = mockStatic(TraineeTrainingSpecification.class);
        mockedStatic.when(() -> TraineeTrainingSpecification.findByCriteria(searchFilter))
                .thenReturn(specification);

        // when
        service.findTrainingByCriteria(searchFilter);

        // then
        verify(trainingService).findAll(searchFilter);

        mockedStatic.close();
    }
}
