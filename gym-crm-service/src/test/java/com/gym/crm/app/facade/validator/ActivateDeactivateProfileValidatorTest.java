package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.ActivateDeactivateProfileRequest;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.assertThat;

class ActivateDeactivateProfileValidatorTest {

    private Errors errors;

    private ActivateDeactivateProfileValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ActivateDeactivateProfileValidator();
    }

    @Test
    @DisplayName("Test valid request functionality")
    void givenValidRequest_whenValidate_thenNoErrors() {
        // given
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();
        errors = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test null fields functionality")
    void givenNullRequest_whenValidate_thenHasErrors() {
        // given
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();
        request.isActive(null);
        errors = new BeanPropertyBindingResult(request, "activateDeactivateProfileRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .contains("isActive.required");
    }

    @Test
    @DisplayName("Test supports activate request functionality")
    void whenSupports_thenReturnsTrue() {
        // when
        boolean actual = validator.supports(ActivateDeactivateProfileRequest.class);

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


}