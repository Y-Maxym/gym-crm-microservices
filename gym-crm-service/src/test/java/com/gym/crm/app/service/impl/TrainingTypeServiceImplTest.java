package com.gym.crm.app.service.impl;

import com.gym.crm.app.entity.TrainingType;
import com.gym.crm.app.repository.TrainingTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository repository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    @DisplayName("Test find all training types functionality")
    void whenFindAll_thenTrainingTypesAreReturned() {
        // given
        TrainingType trainingType = TrainingType.builder().id(1L).trainingTypeName("Yoga").build();
        List<TrainingType> trainingTypes = List.of(trainingType);

        given(repository.findAll())
                .willReturn(trainingTypes);

        // when
        List<TrainingType> actual = trainingTypeService.findAll();

        // then
        assertThat(actual).isEqualTo(trainingTypes);
    }

}