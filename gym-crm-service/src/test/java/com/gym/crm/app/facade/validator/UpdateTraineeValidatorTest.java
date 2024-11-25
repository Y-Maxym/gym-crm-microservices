package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateTraineeValidatorTest {

    private Errors errors;

    private UpdateTraineeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UpdateTraineeValidator();
    }

    @Test
    @DisplayName("Test valid request functionality")
    void givenValidRequest_whenValidate_thenNoErrors() {
        // given
        UpdateTraineeProfileRequest request = EntityTestData.getValidTraineeProfileRequest();
        errors = new BeanPropertyBindingResult(request, "updateTraineeProfileRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test null fields functionality")
    void givenNullRequest_whenValidate_thenHasErrors() {
        // given
        UpdateTraineeProfileRequest request = EntityTestData.getInvalidTraineeProfileRequest();
        errors = new BeanPropertyBindingResult(request, "updateTraineeProfileRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(3);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .contains("last.name.empty.error", "first.name.empty.error", "isActive.empty.error");
    }

    @Test
    @DisplayName("Test supports Trainee request functionality")
    void whenSupports_thenReturnsTrue() {
        // when
        boolean actual = validator.supports(UpdateTraineeProfileRequest.class);

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
        UpdateTraineeProfileRequest request = EntityTestData.getValidTraineeProfileRequest();
        request.setFirstName("long".repeat(100));
        request.setLastName("long".repeat(100));
        errors = new BeanPropertyBindingResult(request, "updateTraineeProfileRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(2);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("first.name.length.error", "last.name.length.error");
    }

    @Test
    @DisplayName("Test fields contain digit chars functionality")
    void givenFieldsContainDigits_whenValidate_thenHasErrors() {
        // given
        UpdateTraineeProfileRequest request = EntityTestData.getValidTraineeProfileRequest();
        request.setFirstName("123");
        request.setLastName("123");
        errors = new BeanPropertyBindingResult(request, "updateTraineeProfileRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(2);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("first.name.digits.error", "last.name.digits.error");
    }
}