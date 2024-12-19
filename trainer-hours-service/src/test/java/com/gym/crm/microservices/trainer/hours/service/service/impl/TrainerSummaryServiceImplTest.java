package com.gym.crm.microservices.trainer.hours.service.service.impl;

import com.gym.crm.microservices.trainer.hours.service.entity.MonthlySummary;
import com.gym.crm.microservices.trainer.hours.service.entity.TrainerSummary;
import com.gym.crm.microservices.trainer.hours.service.entity.YearlySummary;
import com.gym.crm.microservices.trainer.hours.service.exception.DataNotFoundException;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerWorkloadResponse;
import com.gym.crm.microservices.trainer.hours.service.repository.TrainerSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerSummaryServiceImplTest {

    @Mock
    private TrainerSummaryRepository repository;

    @InjectMocks
    private TrainerSummaryServiceImpl service;

    private TrainerSummaryRequest validRequest;
    private TrainerSummary existingTrainerSummary;
    private MonthlySummary monthlySummary;

    @BeforeEach
    void setUp() {
        validRequest = new TrainerSummaryRequest()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(120)
                .actionType(TrainerSummaryRequest.ActionTypeEnum.ADD);

        existingTrainerSummary = new TrainerSummary(null, "John.Doe", "John", "Doe", true, new ArrayList<>());
        YearlySummary yearlySummary = new YearlySummary(null, 2024, new ArrayList<>());
        monthlySummary = new MonthlySummary(null, 5, 0);

        existingTrainerSummary.getYearlySummaries().add(yearlySummary);
        yearlySummary.getMonthlySummaries().add(monthlySummary);
    }

    @Test
    @DisplayName("Test sumTrainerSummary updates the monthly summary correctly")
    void givenValidRequest_whenSumTrainerSummary_thenUpdateMonthlySummary() {
        // given
        given(repository.findByUsername(validRequest.getUsername()))
                .willReturn(Optional.of(existingTrainerSummary));

        // when
        service.sumTrainerSummary(validRequest);

        // then
        assertThat(monthlySummary.getTotalTrainingDuration()).isEqualTo(120);
        verify(repository, times(1)).save(existingTrainerSummary);
    }

    @Test
    @DisplayName("Test sumTrainerSummary creates new trainer if not found")
    void givenNewTrainer_whenSumTrainerSummary_thenCreateNewTrainer() {
        // given
        given(repository.findByUsername(validRequest.getUsername()))
                .willReturn(Optional.empty());

        // when
        service.sumTrainerSummary(validRequest);

        // then
        assertThat(existingTrainerSummary.getUsername()).isEqualTo("John.Doe");
        verify(repository, times(1)).save(existingTrainerSummary);
    }

    @Test
    @DisplayName("Test getTrainerWorkload returns correct workload")
    void givenValidParams_whenGetTrainerWorkload_thenReturnWorkload() {
        // given
        MonthlySummary monthlySummary = new MonthlySummary();
        monthlySummary.setMonth(5);
        monthlySummary.setTotalTrainingDuration(120);

        YearlySummary yearlySummary = new YearlySummary();
        yearlySummary.setYear(2024);
        yearlySummary.getMonthlySummaries().add(monthlySummary);

        TrainerSummary trainerSummary = new TrainerSummary();
        trainerSummary.setUsername("trainer2");
        trainerSummary.setFirstName("firstName");
        trainerSummary.setLastName("lastName");
        trainerSummary.getYearlySummaries().add(yearlySummary);

        List<TrainerSummary> trainerSummaries = List.of(trainerSummary);

        given(repository.findByParams("trainer2", 2024, 5)).willReturn(trainerSummaries);

        // when
        TrainerWorkloadResponse response = service.getTrainerWorkload("trainer2", 2024, 5);

        // then
        assertThat(response.getWorkload()).isEqualTo(120);
    }

    @Test
    @DisplayName("Test getTrainerWorkload throws exception if no workload found")
    void givenNoWorkload_whenGetTrainerWorkload_thenThrowException() {
        // given
        given(repository.findByParams(validRequest.getUsername(), 2024, 5)).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> service.getTrainerWorkload(validRequest.getUsername(), 2024, 5))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("User information not found");
    }

    @Test
    @DisplayName("Test private method getOrCreateTrainerSummary is called when username is found")
    void givenValidRequest_whenGetOrCreateTrainerSummary_thenCallRepository() {
        // given
        given(repository.findByUsername(validRequest.getUsername()))
                .willReturn(Optional.of(existingTrainerSummary));

        // when
        service.sumTrainerSummary(validRequest);

        // then
        verify(repository, times(1)).findByUsername(validRequest.getUsername());
    }

    @Test
    @DisplayName("Test private method createNewTrainerSummary is called when username is not found")
    void givenNonExistentUsername_whenGetOrCreateTrainerSummary_thenCallCreateNew() {
        // given
        given(repository.findByUsername(validRequest.getUsername())).willReturn(Optional.empty());

        // when
        service.sumTrainerSummary(validRequest);

        // then
        verify(repository, times(1)).save(any(TrainerSummary.class));
    }
}