package com.gym.crm.app.it.steps;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.exception.EntityPersistException;
import com.gym.crm.app.facade.ServiceFacade;
import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.rest.model.GetTraineeTrainingsResponse;
import com.gym.crm.app.rest.model.GetTrainerTrainingsResponse;
import com.gym.crm.app.rest.model.GetTrainingTypeResponse;
import com.gym.crm.app.utils.EntityTestData;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class TrainingManagementSteps {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BeanFactory beanFactory;
    private TransactionStatus transactionStatus;

    @Autowired
    private ServiceFacade serviceFacade;

    private AddTrainingRequest addTrainingRequest;
    private BindingResult bindingResult;

    private String username;
    private LocalDate from;
    private LocalDate to;
    private String trainerName;
    private String traineeName;
    private List<GetTraineeTrainingsResponse> traineeTrainings;
    private List<GetTrainerTrainingsResponse> trainerTrainings;
    private List<GetTrainingTypeResponse> trainingTypes;

    @Before
    public void startTransaction() {
        transactionStatus = beanFactory.getBean(PlatformTransactionManager.class)
                .getTransaction(new DefaultTransactionDefinition());
    }

    @After
    public void rollBackTransaction() {
        beanFactory.getBean(PlatformTransactionManager.class)
                .rollback(transactionStatus);
    }

    @Given("I have valid data for creating a training")
    public void i_have_valid_data_for_creating_a_training() {
        addTrainingRequest = EntityTestData.getValidTrainingRequest();
    }

    @When("I send a request to add a training")
    public void i_send_a_request_to_add_a_training() {
        BindingResult bindingResult = new BeanPropertyBindingResult(addTrainingRequest, "addTraining");
        serviceFacade.addTraining(addTrainingRequest, bindingResult);
    }

    @Then("the training should be successfully added")
    public void the_training_should_be_successfully_added() {
        Training actual = entityManager.createQuery("FROM Training t WHERE t.trainingName = :trainingName", Training.class)
                .setParameter("trainingName", addTrainingRequest.getTrainingName())
                .getSingleResult();

        assertThat(actual).isNotNull();
    }

    @Given("I have invalid data for creating a training")
    public void i_have_invalid_data_for_creating_a_training() {
        addTrainingRequest = EntityTestData.getInvalidTrainingRequest();
        bindingResult = new BeanPropertyBindingResult(addTrainingRequest, "addTraining");
        bindingResult.rejectValue("traineeUsername", "trainee.username");
        bindingResult.rejectValue("trainerUsername", "trainer.username");
    }

    @When("I send a request to add a training with invalid data")
    public void i_send_a_request_to_add_a_training_with_invalid_data() {
        assertThrows(EntityPersistException.class, () -> serviceFacade.addTraining(addTrainingRequest, bindingResult));
    }

    @Then("I should be informed about the training creation error")
    public void i_should_be_informed_about_the_training_creation_error() {
        try {
            serviceFacade.addTraining(addTrainingRequest, bindingResult);
        } catch (EntityPersistException ex) {
            assertThat(ex.getMessage()).isEqualTo("Training creation error");
        }
    }

    @Given("I have a username {string}")
    public void i_have_a_username(String username) {
        this.username = username;
    }

    @Given("I have a date range from {string} to {string}")
    public void i_have_a_date_range_from_to(String fromDate, String toDate) {
        from = LocalDate.parse(fromDate);
        to = LocalDate.parse(toDate);
    }

    @Given("I have a trainer name {string}")
    public void i_have_a_trainer_name(String trainerName) {
        this.trainerName = trainerName;
    }

    @When("I request to get trainee trainings by criteria")
    public void i_request_to_get_trainee_trainings_by_criteria() {
        traineeTrainings = serviceFacade.getTraineeTrainingsByCriteria(username, from, to, trainerName, null);
    }

    @Then("I should receive {int} trainee training")
    public void i_should_receive_trainee_training(int expectedSize) {
        assertThat(traineeTrainings).hasSize(expectedSize);
    }

    @Given("I am a trainer with username {string}")
    public void i_am_a_trainer_with_username(String username) {
        this.username = username;
    }

    @Given("I specify a date range from {string} to {string}")
    public void i_specify_a_date_range_from_to(String fromDate, String toDate) {
        from = LocalDate.parse(fromDate);
        to = LocalDate.parse(toDate);
    }

    @Given("I specify a trainee name {string}")
    public void i_specify_a_trainee_name(String traineeName) {
        this.traineeName = traineeName;
    }

    @When("I request to get my trainings by criteria")
    public void i_request_to_get_my_trainings_by_criteria() {
        trainerTrainings = serviceFacade.getTrainerTrainingsByCriteria(username, from, to, traineeName);
    }

    @Then("I should receive {int} trainer training")
    public void i_should_receive_trainer_training(int expectedSize) {
        assertThat(trainerTrainings).hasSize(expectedSize);
    }

    @When("I request to get training types")
    public void i_request_to_get_training_types() {
        trainingTypes = serviceFacade.getTrainingTypes();
    }

    @Then("I should receive {int} training types")
    public void i_should_receive_training_types(int expectedSize) {
        assertThat(trainingTypes).hasSize(expectedSize);
    }
}
