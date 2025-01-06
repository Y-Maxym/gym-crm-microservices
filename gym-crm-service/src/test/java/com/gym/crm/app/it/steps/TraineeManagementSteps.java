package com.gym.crm.app.it.steps;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.AuthenticationException;
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

public class TraineeManagementSteps {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BeanFactory beanFactory;
    private TransactionStatus transactionStatus;

    @Autowired
    private ServiceFacade serviceFacade;

    private TraineeCreateRequest createRequest;
    private UserCredentials userCredentials;
    private BindingResult bindingResult;
    private EntityPersistException persistException;
    private EntityValidationException validationException;
    private AuthenticationException authenticationException;
    private String username;
    private GetTraineeProfileResponse actualResponse;
    private UpdateTraineeProfileRequest updateRequest;

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

    @Given("I have valid data for creating a trainee profile")
    public void i_have_valid_data_for_creating_a_trainee_profile() {
        createRequest = EntityTestData.getValidCreateTraineeProfileRequest();
    }

    @When("I send a request to create a trainee profile using the valid data")
    public void i_send_a_request_to_create_a_trainee_profile_using_the_valid_data() {
        BindingResult bindingResult = new BeanPropertyBindingResult(createRequest, "createTraineeProfile");
        userCredentials = serviceFacade.createTraineeProfile(createRequest, bindingResult);
    }

    @Then("the trainee profile should be successfully created with non-empty username and password")
    public void the_trainee_profile_should_be_successfully_created_with_non_empty_username_and_password() {
        assertThat(userCredentials).isNotNull();
        assertThat(userCredentials.getUsername()).isNotNull().isNotEmpty();
        assertThat(userCredentials.getPassword()).isNotNull().isNotEmpty();
    }

    @Given("I have invalid data for creating a trainee profile")
    public void i_have_invalid_data_for_creating_a_trainee_profile() {
        createRequest = EntityTestData.getInvalidCreateTraineeProfileRequest();
        bindingResult = new BeanPropertyBindingResult(createRequest, "createTraineeProfile");
        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");
    }

    @When("I attempt to create a trainee profile using the invalid data")
    public void i_attempt_to_create_a_trainee_profile_using_the_invalid_data() {
        persistException = assertThrows(EntityPersistException.class, () -> serviceFacade.createTraineeProfile(createRequest, bindingResult));
    }

    @Then("I should be informed about the trainee creation error")
    public void i_should_be_informed_about_the_trainee_creation_error() {
        assertThat(persistException.getMessage()).isEqualTo("Trainee creation error");
    }

    @Given("I have a valid username {string}")
    public void i_have_a_valid_username(String username) {
        this.username = username;
    }

    @When("I request to find the trainee profile by this username")
    public void i_request_to_find_the_trainee_profile_by_this_username() {
        actualResponse = serviceFacade.findTraineeProfileByUsername(username);
    }

    @Then("I should retrieve the trainee profile with first name {string} and last name {string}")
    public void i_should_retrieve_the_trainee_profile_with_first_name_and_last_name(String firstName, String lastName) {
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getFirstName()).isEqualTo(firstName);
        assertThat(actualResponse.getLastName()).isEqualTo(lastName);
    }

    @Given("I have an invalid username {string}")
    public void i_have_an_invalid_username(String username) {
        this.username = username;
    }

    @When("I attempt to find the trainee profile by this username")
    public void i_attempt_to_find_the_trainee_profile_by_this_username() {
        validationException = assertThrows(EntityValidationException.class, () -> serviceFacade.findTraineeProfileByUsername(username));
    }

    @Then("I should be informed that the trainee profile was not found")
    public void i_should_be_informed_that_the_trainee_profile_was_not_found() {
        assertThat(validationException.getMessage()).isEqualTo("Trainee with username %s not found".formatted(username));
    }

    @Given("I have valid data to update a trainee profile for username {string}")
    public void i_have_valid_data_to_update_a_trainee_profile(String username) {
        this.username = username;
        updateRequest = EntityTestData.getValidTraineeProfileRequest();
    }

    @When("I request to update the trainee profile with valid data")
    public void i_request_to_update_the_trainee_profile_with_valid_data() {
        BindingResult bindingResult = new BeanPropertyBindingResult(updateRequest, "updateTraineeProfile");
        User user = EntityTestData.getPersistedUserJohnDoe();
        serviceFacade.updateTraineeProfile(username, updateRequest, bindingResult, user);
    }

    @Then("the trainee profile should be successfully updated")
    public void the_trainee_profile_should_be_successfully_updated() {
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo(updateRequest.getFirstName());
    }

    @Given("I have invalid data to update a trainee profile for username {string}")
    public void i_have_invalid_data_to_update_a_trainee_profile(String username) {
        this.username = username;
        updateRequest = EntityTestData.getInvalidTraineeProfileRequest();
    }

    @When("I attempt to update the trainee profile with invalid data")
    public void i_attempt_to_update_the_trainee_profile_with_invalid_data() {
        BindingResult bindingResult = new BeanPropertyBindingResult(updateRequest, "updateTraineeProfile");
        bindingResult.rejectValue("firstName", "invalid.firstName");
        bindingResult.rejectValue("lastName", "invalid.lastName");

        persistException = assertThrows(EntityPersistException.class, () -> serviceFacade.updateTraineeProfile(username, updateRequest, bindingResult, EntityTestData.getPersistedUserEmilyDavis()));
    }

    @Then("I should be informed about the trainee update error")
    public void i_should_be_informed_about_the_trainee_update_error() {
        assertThat(persistException.getMessage()).isEqualTo("Trainee update error");
    }

    @Given("a trainee profile exists with username {string}")
    public void a_trainee_profile_exists_with_username(String username) {
        this.username = username;
    }

    @When("I delete the trainee profile by username {string}")
    public void i_delete_the_trainee_profile_by_username(String username) {
        serviceFacade.deleteTraineeProfileByUsername(username, EntityTestData.getPersistedUserJohnDoe());
    }

    @Then("the trainee profile should be deleted")
    public void the_trainee_profile_should_be_deleted() {
        List<Trainee> actual = entityManager.createQuery("FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultList();

        assertThat(actual).isEmpty();
    }

    @Given("a trainee profile non exists with username {string}")
    public void a_trainee_profile_non_exists_with_username(String username) {
        this.username = username;
    }

    @When("I attempt to delete the trainee profile by username {string}")
    public void i_attempt_to_delete_the_trainee_profile_by_username(String username) {
        authenticationException = assertThrows(AuthenticationException.class, () -> serviceFacade.deleteTraineeProfileByUsername(username, EntityTestData.getPersistedUserDavidBrown()));
    }

    @Then("I should receive an error message {string}")
    public void i_should_receive_an_error_message(String expectedMessage) {
        assertThat(authenticationException.getMessage()).isEqualTo(expectedMessage);
    }

    @Given("a trainee with username {string} exists")
    public void a_trainee_with_username_exists(String username) {
        this.username = username;
    }

    @When("I add a new trainer to the trainee {string}")
    public void i_add_a_new_trainer_to_the_trainee(String username) {
        List<TrainerProfileOnlyUsername> request = EntityTestData.getValidListTrainerProfileOnlyUsernames();
        serviceFacade.updateTraineesTrainerList(username, request, EntityTestData.getPersistedUserJohnDoe());
    }

    @Then("the trainee {string} should have {int} trainers")
    public void the_trainee_should_have_trainers(String username, int expectedNumberOfTrainers) {
        List<Trainer> actual = entityManager.createQuery("SELECT t.trainers FROM Trainee t WHERE t.user.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultList();

        assertThat(actual).hasSize(expectedNumberOfTrainers);
    }
}
