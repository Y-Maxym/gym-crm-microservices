package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.ChangePasswordRequest;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordValidatorTest {

    private Errors errors;

    private ChangePasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ChangePasswordValidator();
    }

    @Test
    @DisplayName("Test valid request functionality")
    void givenValidRequest_whenValidate_thenNoErrors() {
        // given
        ChangePasswordRequest request = EntityTestData.getValidChangePasswordRequest();
        errors = new BeanPropertyBindingResult(request, "changePasswordRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test null fields functionality")
    void givenNullRequest_whenValidate_thenHasErrors() {
        // given
        ChangePasswordRequest request = EntityTestData.getNullChangePasswordRequest();
        errors = new BeanPropertyBindingResult(request, "changePasswordRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(3);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .contains("username.empty.error", "password.empty.error", "new.password.empty.error");
    }

    @Test
    @DisplayName("Test supports change password request functionality")
    void whenSupports_thenReturnsTrue() {
        // when
        boolean actual = validator.supports(ChangePasswordRequest.class);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Test supports Trainer request functionality")
    void whenSupports_thenReturnsFalse() {
        // when
        boolean actual = validator.supports(TrainerCreateRequest.class);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Test fields longer than 100 chars functionality")
    void givenLongLastName_whenValidate_thenHasErrors() {
        // given
        ChangePasswordRequest request = EntityTestData.getValidChangePasswordRequest();
        request.setUsername("long".repeat(100));
        request.setPassword("long".repeat(100));
        request.setNewPassword("long".repeat(100));
        errors = new BeanPropertyBindingResult(request, "changePasswordRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(3);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("username.length.error", "password.length.error", "new.password.length.error");
    }
}