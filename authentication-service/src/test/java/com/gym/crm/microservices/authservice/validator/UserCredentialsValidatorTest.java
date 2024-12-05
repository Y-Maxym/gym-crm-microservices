package com.gym.crm.microservices.authservice.validator;

import com.gym.crm.microservices.authservice.model.UserCredentials;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.assertThat;

class UserCredentialsValidatorTest {

    private Errors errors;

    private UserCredentialsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserCredentialsValidator();
    }

    @Test
    @DisplayName("Test valid request functionality")
    void givenValidRequest_whenValidate_thenNoErrors() {
        // given
        UserCredentials request = EntityTestData.getValidJohnDoeAuthCredentials();
        errors = new BeanPropertyBindingResult(request, "userCredentials");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test null fields functionality")
    void givenNullRequest_whenValidate_thenHasErrors() {
        // given
        UserCredentials request = EntityTestData.getNullAuthCredentials();
        errors = new BeanPropertyBindingResult(request, "userCredentials");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(2);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .contains("username.empty.error", "password.empty.error");
    }

    @Test
    @DisplayName("Test supports user credentials request functionality")
    void whenSupports_thenReturnsTrue() {
        // when
        boolean actual = validator.supports(UserCredentials.class);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Test fields longer than 100 chars functionality")
    void givenLongLastName_whenValidate_thenHasErrors() {
        // given
        UserCredentials request = EntityTestData.getValidJohnDoeAuthCredentials();
        request.setUsername("long".repeat(100));
        request.setPassword("long".repeat(100));
        errors = new BeanPropertyBindingResult(request, "userCredentials");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(2);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("username.length.error", "password.length.error");
    }
}