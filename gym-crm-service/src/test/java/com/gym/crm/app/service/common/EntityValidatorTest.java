package com.gym.crm.app.service.common;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.gym.crm.app.util.Constants.ERROR_ENTITY_CANNOT_BE_NULL;
import static com.gym.crm.app.util.Constants.ERROR_ENTITY_ID_CANNOT_BE_NULL;
import static com.gym.crm.app.util.Constants.ERROR_ENTITY_ID_CANNOT_BE_ZERO;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EntityValidatorTest {

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private EntityValidator validator;

    @Test
    @DisplayName("Test check null id functionality")
    void givenNullId_whenCheckId_thenExceptionIsThrows() {
        // given
        given(messageHelper.getMessage(ERROR_ENTITY_ID_CANNOT_BE_NULL))
                .willReturn("Entity id cannot be null");

        // when && then
        assertThatThrownBy(() -> validator.checkId(null))
                .isInstanceOf(EntityValidationException.class)
                .hasMessage("Entity id cannot be null");
    }

    @Test
    @DisplayName("Test check zero id functionality")
    void givenZeroId_whenCheckId_thenExceptionIsThrows() {
        // given
        long id = 0L;
        given(messageHelper.getMessage(ERROR_ENTITY_ID_CANNOT_BE_ZERO))
                .willReturn("Entity id cannot be less or equals zero");

        // when && then
        assertThatThrownBy(() -> validator.checkId(id))
                .isInstanceOf(EntityValidationException.class)
                .hasMessage("Entity id cannot be less or equals zero");
    }

    @Test
    @DisplayName("Test check negative id functionality")
    void givenNegativeId_whenCheckId_thenExceptionIsThrows() {
        // given
        long id = -1L;
        given(messageHelper.getMessage(ERROR_ENTITY_ID_CANNOT_BE_ZERO))
                .willReturn("Entity id cannot be less or equals zero");

        // when && then
        assertThatThrownBy(() -> validator.checkId(id))
                .isInstanceOf(EntityValidationException.class)
                .hasMessage("Entity id cannot be less or equals zero");
    }

    @Test
    @DisplayName("Test check valid id functionality")
    void givenValidId_whenCheckId_thenExceptionIsThrows() {
        // given
        long id = 1L;

        // when
        validator.checkId(id);

        // then
        assertDoesNotThrow(() -> validator.checkId(id));
    }

    @Test
    @DisplayName("Test check null entity functionality")
    public void givenNullEntity_whenCheckEntity_thenExceptionIsThrows() {
        // given
        given(messageHelper.getMessage(ERROR_ENTITY_CANNOT_BE_NULL))
                .willReturn("Entity cannot be null");

        // when & then
        assertThatThrownBy(() -> validator.checkEntity(null))
                .isInstanceOf(EntityValidationException.class)
                .hasMessage("Entity cannot be null");
    }

    @Test
    @DisplayName("Test check valid entity functionality")
    public void givenValidEntity_whenCheckEntity_thenSuccess() {
        // given
        Object entity = new Object();

        // when
        validator.checkEntity(entity);

        // then
        assertDoesNotThrow(() -> validator.checkEntity(entity));
    }

    @Test
    @DisplayName("Test check exists trainee by username functionality")
    public void givenUsername_whenCheckIfTraineeExist_thenTraineeIsFound() {
        // given
        Trainee expected = EntityTestData.getTransientTraineeJohnDoe();

        given(traineeRepository.findByUserUsername(expected.getUser().getUsername()))
                .willReturn(Optional.of(expected));

        // when && then
        assertDoesNotThrow(() -> validator.checkIfTraineeExist(expected.getUser().getUsername()));
    }

    @Test
    @DisplayName("Test check exists trainee by incorrect username functionality")
    public void givenIncorrectUsername_whenCheckIfTraineeExist_thenTraineeIsNotFound() {
        // given
        Trainee expected = EntityTestData.getTransientTraineeJohnDoe();

        given(traineeRepository.findByUserUsername(expected.getUser().getUsername()))
                .willReturn(Optional.empty());

        // when && then
        assertThrows(EntityValidationException.class, () -> validator.checkIfTraineeExist(expected.getUser().getUsername()));
    }

    @Test
    @DisplayName("Test check exists trainee by username functionality")
    public void givenUsername_whenCheckIfTrainerExist_thenTrainerIsFound() {
        // given
        Trainer expected = EntityTestData.getTransientTrainerEmilyDavis();

        given(trainerRepository.findByUserUsername(expected.getUser().getUsername()))
                .willReturn(Optional.of(expected));

        // when && then
        assertDoesNotThrow(() -> validator.checkIfTrainerExist(expected.getUser().getUsername()));
    }

    @Test
    @DisplayName("Test check exists trainee by incorrect username functionality")
    public void givenIncorrectUsername_whenCheckIfTrainerExist_thenTrainerIsNotFound() {
        // given
        Trainee expected = EntityTestData.getTransientTraineeJohnDoe();

        given(trainerRepository.findByUserUsername(expected.getUser().getUsername()))
                .willReturn(Optional.empty());

        // when && then
        assertThrows(EntityValidationException.class, () -> validator.checkIfTrainerExist(expected.getUser().getUsername()));
    }
}
