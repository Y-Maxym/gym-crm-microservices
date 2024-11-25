package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.TraineeCreateRequest;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTraineeValidatorTest {

    private Errors errors;

    private CreateTraineeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateTraineeValidator();
    }

    @Test
    @DisplayName("Test valid request functionality")
    void givenValidRequest_whenValidate_thenNoErrors() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        errors = new BeanPropertyBindingResult(request, "traineeCreateRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test null fields functionality")
    void givenNullRequest_whenValidate_thenHasErrors() {
        // given
        TraineeCreateRequest request = EntityTestData.getInvalidCreateTraineeProfileRequest();
        errors = new BeanPropertyBindingResult(request, "traineeCreateRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(2);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .contains("last.name.empty.error", "first.name.empty.error");
    }

    @Test
    @DisplayName("Test supports Trainee request functionality")
    void whenSupports_thenReturnsTrue() {
        // when
        boolean actual = validator.supports(TraineeCreateRequest.class);

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
    @DisplayName("Test empty first name functionality")
    void givenEmptyFirstName_whenValidate_thenHasErrors() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        request.setFirstName("");
        errors = new BeanPropertyBindingResult(request, "traineeCreateRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("first.name.empty.error");
    }

    @Test
    @DisplayName("Test empty last name functionality")
    void givenEmptyLastName_whenValidate_thenHasErrors() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        request.setLastName("");
        errors = new BeanPropertyBindingResult(request, "traineeCreateRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("last.name.empty.error");
    }

    @Test
    @DisplayName("Test first name longer than 50 chars functionality")
    void givenLongFirstName_whenValidate_thenHasErrors() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        request.setFirstName("long".repeat(50));
        errors = new BeanPropertyBindingResult(request, "traineeCreateRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("first.name.length.error");
    }

    @Test
    @DisplayName("Test last name longer than 50 chars functionality")
    void givenLongLastName_whenValidate_thenHasErrors() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        request.setLastName("long".repeat(50));
        errors = new BeanPropertyBindingResult(request, "traineeCreateRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("last.name.length.error");
    }

    @Test
    @DisplayName("Test fields contain digit chars functionality")
    void givenFieldsContainDigits_whenValidate_thenHasErrors() {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();
        request.setFirstName("123");
        request.setLastName("123");
        errors = new BeanPropertyBindingResult(request, "traineeCreateRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(2);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("first.name.digits.error", "last.name.digits.error");
    }
}