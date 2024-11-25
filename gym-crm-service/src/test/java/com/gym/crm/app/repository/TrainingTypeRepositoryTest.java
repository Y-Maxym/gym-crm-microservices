package com.gym.crm.app.repository;

import com.gym.crm.app.entity.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTypeRepositoryTest extends AbstractTestRepository<TrainingTypeRepository> {

    @Test
    @DisplayName("Test find training type by name functionality")
    public void givenName_whenFindByTrainingTypeName_thenTypeIsFound() {
        // when
        Optional<TrainingType> actual = repository.findByTrainingTypeName("Fitness");

        // then
        assertThat(actual.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Test find training type by incorrect name functionality")
    public void givenIncorrectName_whenFindByUsername_thenTypeIsNotFound() {
        // when
        Optional<TrainingType> actual = repository.findByTrainingTypeName("training type");

        // then
        assertThat(actual.isEmpty()).isTrue();
    }
}