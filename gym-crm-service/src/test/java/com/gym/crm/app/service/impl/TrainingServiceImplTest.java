package com.gym.crm.app.service.impl;

import com.gym.crm.app.client.TrainerHoursClient;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.service.common.EntityValidator;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.gym.crm.app.util.Constants.ERROR_TRAINING_WITH_ID_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private TrainerHoursClient client;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private TrainingRepository repository;

    @InjectMocks
    private TrainingServiceImpl service;

    @Test
    @DisplayName("Test find training by id functionality")
    public void givenId_whenFindById_thenTrainingIsReturned() {
        // given
        Training expected = EntityTestData.getPersistedTrainingEmilyDavis();
        long id = expected.getId();

        doNothing().when(entityValidator).checkId(id);
        given(repository.findById(id))
                .willReturn(Optional.of(expected));

        // when
        Training actual = service.findById(id);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find training by incorrect id functionality")
    public void givenIncorrectId_whenFindById_thenExceptionIsThrown() {
        // given
        long id = 1L;
        String message = "Training with id %s not found".formatted(id);

        doNothing().when(entityValidator).checkId(id);
        given(repository.findById(id))
                .willReturn(Optional.empty());
        given(messageHelper.getMessage(ERROR_TRAINING_WITH_ID_NOT_FOUND, id))
                .willReturn(message);

        // when
        EntityValidationException ex = assertThrows(EntityValidationException.class, () -> service.findById(id));

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Test save training functionality")
    public void givenSaveTraining_whenSave_thenRepositoryIsCalled() {
        // given
        Training training = EntityTestData.getPersistedTrainingEmilyDavis();

        doNothing().when(entityValidator).checkEntity(training);
        given(repository.save(training))
                .willReturn(training);

        // when
        service.save(training);

        // then
        verify(repository, only()).save(training);
    }
}
