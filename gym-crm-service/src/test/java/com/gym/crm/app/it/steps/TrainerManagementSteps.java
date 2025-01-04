package com.gym.crm.app.it.steps;

import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.EntityPersistException;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.facade.ServiceFacade;
import com.gym.crm.app.rest.model.*;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrainerManagementSteps {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BeanFactory beanFactory;
    private TransactionStatus transactionStatus;

    @Autowired
    private ServiceFacade serviceFacade;

    private TrainerCreateRequest createRequest;
    private UpdateTrainerProfileRequest updateRequest;
    private BindingResult bindingResult;
    private UserCredentials actualCredentials;
    private EntityPersistException persistException;
    private EntityValidationException validationException;
    private String username;
    private GetTrainerProfileResponse response;
    private List<TrainerProfileWithUsername> resultList;

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

    @Given("I have a valid data for creating a trainer profile")
    public void i_have_valid_data_for_creating_a_trainer_profile() {
        createRequest = EntityTestData.getValidCreateTrainerProfileRequest();
    }

    @When("I send a request to create a trainer profile using the valid data")
    public void i_send_a_request_to_create_a_trainer_profile_using_the_valid_data() {
        BindingResult bindingResult = new BeanPropertyBindingResult(createRequest, "createTrainerProfileRequest");
        actualCredentials = serviceFacade.createTrainerProfile(createRequest, bindingResult);
    }

    @Then("the trainer profile should be successfully created with non-empty username and password")
    public void the_trainer_profile_should_be_successfully_created_with_non_empty_username_and_password() {
        assertThat(actualCredentials).isNotNull();
        assertThat(actualCredentials.getUsername()).isNotNull().isNotEmpty();
        assertThat(actualCredentials.getPassword()).isNotNull().isNotEmpty();
    }

    @Given("I have invalid data for creating a trainer profile")
    public void i_have_invalid_data_for_creating_a_trainer_profile() {
        createRequest = EntityTestData.getInvalidCreateTrainerProfileRequest();
        bindingResult = new BeanPropertyBindingResult(createRequest, "createTrainerProfile");
        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");
    }

    @When("I attempt to create a trainer profile using the invalid data")
    public void i_attempt_to_create_a_trainer_profile_using_the_invalid_data() {
        persistException = assertThrows(EntityPersistException.class, () -> serviceFacade.createTrainerProfile(createRequest, bindingResult));
    }

    @Then("I should be informed about the trainer creation error")
    public void i_should_be_informed_about_the_trainer_creation_error() {
        assertThat(persistException.getMessage()).isEqualTo("Trainer creation error");
    }

    @Given("there is a trainer profile with username {string}")
    public void there_is_a_trainer_profile_with_username(String username) {
        this.username = username;
    }

    @When("I request to find the trainer profile by username {string}")
    public void i_request_to_find_the_trainer_profile_by_username(String username) {
        response = serviceFacade.findTrainerProfileByUsername(username);
    }

    @Then("I should retrieve the trainer profile with first name {string} and last name {string}")
    public void i_should_retrieve_the_trainer_profile_with_first_name_and_last_name(String firstName, String lastName) {
        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo(firstName);
        assertThat(response.getLastName()).isEqualTo(lastName);
    }

    @Given("I have the username {string}")
    public void i_have_the_username(String username) {
        this.username = username;
    }

    @When("I attempt to find the trainer profile by this username")
    public void i_attempt_to_find_the_trainer_profile_by_this_username() {
        validationException = assertThrows(EntityValidationException.class,
                () -> serviceFacade.findTrainerProfileByUsername(username));
    }

    @Then("I should be informed that the trainer profile was not found")
    public void i_should_be_informed_that_the_trainer_profile_was_not_found() {
        assertThat(validationException.getMessage()).isEqualTo("Trainer with username %s not found".formatted(username));
    }

    @Given("I have valid data to update a trainer profile for username {string}")
    public void i_have_valid_data_to_update_a_trainer_profile(String username) {
        this.username = username;
        updateRequest = EntityTestData.getValidUpdateTrainerProfileRequest();
    }

    @When("I request to update the trainer profile with valid data")
    public void i_request_to_update_the_trainer_profile_with_valid_data() {
        User user = EntityTestData.getPersistedUserEmilyDavis();
        BindingResult bindingResult = new BeanPropertyBindingResult(updateRequest, "updateTrainerProfile");
        serviceFacade.updateTrainerProfile(username, updateRequest, bindingResult, user);
    }

    @Then("the trainer profile should be successfully updated")
    public void the_trainer_profile_should_be_successfully_updated() {
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", this.username)
                .getSingleResult();

        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo(updateRequest.getFirstName());
    }

    @Given("I have invalid data to update a trainer profile for username {string}")
    public void i_have_invalid_data_to_update_a_trainer_profile(String username) {
        this.username = username;
        updateRequest = EntityTestData.getInvalidTrainerProfileRequest();
        bindingResult = new BeanPropertyBindingResult(updateRequest, "updateTrainerProfile");
        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");
    }

    @When("I attempt to update the trainer profile with invalid data")
    public void i_attempt_to_update_the_trainer_profile_with_invalid_data() {
        User user = EntityTestData.getPersistedUserEmilyDavis();
        persistException = assertThrows(EntityPersistException.class,
                () -> serviceFacade.updateTrainerProfile(username, updateRequest, bindingResult, user));
    }

    @Then("I should be informed about the trainer update error")
    public void i_should_be_informed_about_the_trainer_update_error() {
        assertThat(persistException.getMessage()).isEqualTo("Trainer update error");
    }

    @Given("the trainee {string} exists")
    public void the_trainee_exists(String username) {
        this.username = username;
    }

    @When("I request a list of trainers not assigned to {string}")
    public void i_request_a_list_of_trainers_not_assigned_to(String username) {
        resultList = serviceFacade.getTrainersNotAssignedByTraineeUsername(username);
    }

    @Then("I should receive a list of trainers with a size of {int}")
    public void i_should_receive_a_list_of_trainers_with_a_size_of(int expectedSize) {
        assertThat(resultList).hasSize(expectedSize);
    }
}
