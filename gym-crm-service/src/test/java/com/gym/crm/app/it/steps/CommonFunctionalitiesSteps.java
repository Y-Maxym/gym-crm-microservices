package com.gym.crm.app.it.steps;

import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.AuthenticationException;
import com.gym.crm.app.facade.ServiceFacade;
import com.gym.crm.app.rest.model.ActivateDeactivateProfileRequest;
import com.gym.crm.app.rest.model.ChangePasswordRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.service.common.UserProfileService;
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
import org.springframework.security.core.Authentication;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommonFunctionalitiesSteps {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BeanFactory beanFactory;
    private TransactionStatus transactionStatus;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ServiceFacade serviceFacade;

    private BindingResult bindingResult;
    private ChangePasswordRequest passwordRequest;
    private User user;
    private String username;
    private ActivateDeactivateProfileRequest activateProfileRequest = EntityTestData.getActivateProfileRequest();
    private ActivateDeactivateProfileRequest deactivateProfileRequest = EntityTestData.getDeactivateProfileRequest();
    private UserCredentials credentials;
    private Authentication actual;

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

    @Given("I have valid change password credentials")
    public void i_have_valid_change_password_credentials() {
        passwordRequest = EntityTestData.getValidChangePasswordRequest();
        user = EntityTestData.getPersistedUserEmilyDavis();
        bindingResult = new BeanPropertyBindingResult(passwordRequest, "changePasswordRequest");
    }

    @When("I request to change the password")
    public void i_request_to_change_the_password() {
        serviceFacade.changePassword(passwordRequest, bindingResult, user);
    }

    @Then("the password should be successfully changed")
    public void the_password_should_be_successfully_changed() {
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", passwordRequest.getUsername())
                .getSingleResult();

        assertTrue(userProfileService.isPasswordCorrect(passwordRequest.getNewPassword(), actual.getPassword()));
    }

    @Given("I have valid change password credentials and invalid user")
    public void i_have_valid_change_password_credentials_and_invalid_user() {
        passwordRequest = EntityTestData.getValidChangePasswordRequest();
        user = EntityTestData.getPersistedUserJohnDoe();
        bindingResult = new BeanPropertyBindingResult(passwordRequest, "changePasswordRequest");
    }

    @When("I attempt to change the password")
    public void i_attempt_to_change_the_password() {
        assertThrows(AuthenticationException.class, () -> serviceFacade.changePassword(passwordRequest, bindingResult, user));
    }

    @Then("I should receive an error message {string} with invalid user")
    public void i_should_receive_an_error_message_with_invalid_user(String expectedMessage) {
        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> serviceFacade.changePassword(passwordRequest, bindingResult, user));
        assertThat(ex.getMessage()).isEqualTo(expectedMessage);
    }

    @Given("I have invalid change password credentials")
    public void i_have_invalid_change_password_credentials() {
        passwordRequest = EntityTestData.getInvalidChangePasswordRequest();
        user = EntityTestData.getTransientUserEmilyDavis();
        bindingResult = new BeanPropertyBindingResult(passwordRequest, "changePasswordRequest");
    }

    @When("I attempt to change the password with invalid credentials")
    public void i_attempt_to_change_the_password_with_invalid_credentials() {
        assertThrows(AuthenticationException.class, () -> serviceFacade.changePassword(passwordRequest, bindingResult, user));
    }

    @Then("I should receive an error message {string} with invalid credentials")
    public void i_should_receive_an_error_message_with_invalid_credentials(String expectedMessage) {
        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> serviceFacade.changePassword(passwordRequest, bindingResult, user));
        assertThat(ex.getMessage()).isEqualTo(expectedMessage);
    }

    @Given("I have an activated profile with username {string}")
    public void i_have_an_activated_profile_with_username(String username) {
        this.username = username;
        user = EntityTestData.getPersistedUserJohnDoe();
        bindingResult = new BeanPropertyBindingResult(activateProfileRequest, "activateDeactivateProfileRequest");
    }

    @When("I request to activate the profile for {string}")
    public void i_request_to_activate_the_profile_for(String username) {
        serviceFacade.activateDeactivateProfile(username, activateProfileRequest, bindingResult, user);
    }

    @Then("the profile for {string} should remain activated")
    public void the_profile_for_should_remain_activated(String username) {
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertTrue(actual.isActive());
    }

    @Given("I have a deactivated profile with activated username {string}")
    public void i_have_a_deactivated_profile_with_activate_username(String username) {
        this.username = username;
        entityManager.createQuery("UPDATE User u SET u.isActive = false WHERE u.username = :username")
                .setParameter("username", username)
                .executeUpdate();
    }

    @When("I request to activate the profile for {string} with deactivated profile")
    public void i_request_to_activate_the_profile_with_deactivated_profile(String username) {
        activateProfileRequest = EntityTestData.getActivateProfileRequest();
        bindingResult = new BeanPropertyBindingResult(activateProfileRequest, "activateDeactivateProfileRequest");
        user = EntityTestData.getPersistedUserJohnDoe();
        serviceFacade.activateDeactivateProfile(username, activateProfileRequest, bindingResult, user);
    }

    @Then("the profile for {string} should be activated")
    public void the_profile_for_should_be_activated(String username) {
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertTrue(actual.isActive());
    }

    @Given("I have a deactivated profile with deactivated username {string}")
    public void i_have_a_deactivated_profile_with_deactivate_username(String username) {
        this.username = username;
        bindingResult = new BeanPropertyBindingResult(deactivateProfileRequest, "deactivateDeactivateProfileRequest");
        user = EntityTestData.getPersistedUserJohnDoe();
    }

    @When("I request to deactivate the profile for {string}")
    public void i_request_to_deactivate_the_profile_for(String username) {
        serviceFacade.activateDeactivateProfile(username, deactivateProfileRequest, bindingResult, user);
    }

    @Then("the profile for {string} should remain deactivated")
    public void the_profile_for_should_remain_deactivated(String username) {
        User actual = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        assertFalse(actual.isActive());
    }

    @Given("I have valid user credentials")
    public void i_have_valid_user_credentials() {
        credentials = EntityTestData.getValidJohnDoeAuthCredentials();
    }

    @When("I request to authenticate")
    public void i_request_to_authenticate() {
        BindingResult bindingResult = new BeanPropertyBindingResult(credentials, "userCredentials");
        actual = serviceFacade.authenticate(credentials, bindingResult);
    }

    @Then("authentication should be successful")
    public void authentication_should_be_successful() {
        assertThat(actual).isNotNull();
    }
}
