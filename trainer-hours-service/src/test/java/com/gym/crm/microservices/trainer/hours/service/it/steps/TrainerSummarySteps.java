package com.gym.crm.microservices.trainer.hours.service.it.steps;

import com.gym.crm.microservices.trainer.hours.service.entity.MonthlySummary;
import com.gym.crm.microservices.trainer.hours.service.entity.TrainerSummary;
import com.gym.crm.microservices.trainer.hours.service.entity.YearlySummary;
import com.gym.crm.microservices.trainer.hours.service.exception.DataNotFoundException;
import com.gym.crm.microservices.trainer.hours.service.repository.TrainerSummaryRepository;
import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerWorkloadResponse;
import com.gym.crm.microservices.trainer.hours.service.service.TrainerSummaryService;
import com.gym.crm.microservices.trainer.hours.service.utils.EntityTestData;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrainerSummarySteps {

    @Autowired
    private TrainerSummaryRepository repository;

    @Autowired
    private TrainerSummaryService service;

    private TrainerSummaryRequest validRequest;
    private TrainerWorkloadResponse trainerWorkload;
    private DataNotFoundException exception;

    @Before
    public void before() {
        repository.deleteAll();
    }

    @Given("a valid request for updating trainer summary")
    public void a_valid_request_for_updating_trainer_summary() {
        validRequest = EntityTestData.getValidTrainerSummaryRequest();
    }

    @When("the sumTrainerSummary is executed")
    public void the_sumTrainerSummary_is_executed() {
        service.sumTrainerSummary(validRequest);
    }

    @Then("the monthly summary is updated correctly and equals {int}")
    public void the_monthly_summary_is_updated_correctly(int duration) {
        TrainerSummary trainerSummary = repository.findByUsername(validRequest.getUsername()).orElseThrow();
        Optional<Integer> totalTrainingDuration = trainerSummary.getYearlySummaries().stream()
                .map(YearlySummary::getMonthlySummaries)
                .flatMap(Collection::stream)
                .map(MonthlySummary::getTotalTrainingDuration)
                .reduce(Integer::sum);

        assertThat(totalTrainingDuration.isPresent()).isTrue();
        assertThat(totalTrainingDuration.get()).isEqualTo(duration);
        assertThat(validRequest.getUsername()).isEqualTo(trainerSummary.getUsername());
    }

    @Given("the trainer John.Doe has a total training duration of 120")
    public void the_trainer_John_Doe_has_a_total_training_duration_of_120() {
        validRequest = EntityTestData.getValidTrainerSummaryRequest();

        service.sumTrainerSummary(validRequest);
    }

    @When("I request the workload for trainer {string} year {int} month {int}")
    public void i_request_the_workload_for_trainer(String username, int year, int month) {
        trainerWorkload = service.getTrainerWorkload(username, year, month);
    }

    @Then("the workload returned should be {int}")
    public void the_workload_returned_should_be(int expected) {
        assertThat(trainerWorkload.getWorkload()).isEqualTo(expected);
    }

    @When("I request the workload for trainer {string} for {int} year {int}")
    public void i_request_the_workload_for_trainer_for_May_2024(String username, int year, int month) {
        exception = assertThrows(DataNotFoundException.class, () -> service.getTrainerWorkload(username, year, month));
    }

    @Then("an exception should be thrown indicating {string}")
    public void an_exception_should_be_thrown_indicating(String message) {
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
