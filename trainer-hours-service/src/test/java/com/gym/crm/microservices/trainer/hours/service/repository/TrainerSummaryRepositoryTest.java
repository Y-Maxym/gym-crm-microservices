package com.gym.crm.microservices.trainer.hours.service.repository;

import com.gym.crm.microservices.trainer.hours.service.entity.MonthlySummary;
import com.gym.crm.microservices.trainer.hours.service.entity.TrainerSummary;
import com.gym.crm.microservices.trainer.hours.service.entity.YearlySummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class TrainerSummaryRepositoryTest extends MongoDbTestContainerConfiguration {

    @Autowired
    private TrainerSummaryRepository repository;

    @Test
    @DisplayName("Test findByUsername returns TrainerSummary when username exists")
    void givenUsername_whenFindByUsername_thenReturnTrainerSummary() {
        // given
        TrainerSummary trainerSummary = new TrainerSummary();
        trainerSummary.setUsername("trainer1");
        trainerSummary.setFirstName("firstName");
        trainerSummary.setLastName("lastName");
        repository.save(trainerSummary);

        // when
        Optional<TrainerSummary> result = repository.findByUsername("trainer1");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("trainer1");
    }

    @Test
    @DisplayName("Test findByUsername returns empty when username does not exist")
    void givenNonExistentUsername_whenFindByUsername_thenReturnEmpty() {
        // given
        String nonExistentUsername = "nonExistentUsername";

        // when
        Optional<TrainerSummary> result = repository.findByUsername(nonExistentUsername);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test findWorkloadByParams returns correct workload value")
    void givenUsernameYearAndMonth_whenFindByParams_thenReturnCorrectWorkload() {
        // given
        YearlySummary yearlySummary = new YearlySummary();
        MonthlySummary monthlySummary = new MonthlySummary();
        TrainerSummary trainerSummary = new TrainerSummary();

        trainerSummary.setUsername("trainer2");
        trainerSummary.setFirstName("firstName");
        trainerSummary.setLastName("lastName");

        yearlySummary.setYear(2024);

        monthlySummary.setMonth(5);
        monthlySummary.setTotalTrainingDuration(120);

        yearlySummary.getMonthlySummaries().add(monthlySummary);
        trainerSummary.getYearlySummaries().add(yearlySummary);

        repository.save(trainerSummary);

        // when
        List<TrainerSummary> result = repository.findByParams("trainer2", 2024, 5);
        int workload = result.stream()
                .flatMap(ts -> ts.getYearlySummaries().stream())
                .flatMap(ys -> ys.getMonthlySummaries().stream())
                .mapToInt(MonthlySummary::getTotalTrainingDuration)
                .sum();

        // then
        assertThat(workload).isEqualTo(120);
    }

    @Test
    @DisplayName("Test findWorkloadByParams returns null when no matching data found")
    void givenNoMatchingData_whenFindByParams_thenReturnNull() {
        // given
        String username = "trainer3";
        Integer year = 2024;
        Integer month = 6;

        // when
        List<TrainerSummary> result = repository.findByParams(username, year, month);
        int workload = result.stream()
                .flatMap(ts -> ts.getYearlySummaries().stream())
                .flatMap(ys -> ys.getMonthlySummaries().stream())
                .mapToInt(MonthlySummary::getTotalTrainingDuration)
                .sum();

        // then
        assertThat(workload).isEqualTo(0);
    }
}