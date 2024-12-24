package com.gym.crm.microservices.trainer.hours.service.validator;

import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TrainerSummaryRequestValidatorTest {

    private Errors errors;

    @InjectMocks
    private TrainerSummaryRequestValidator validator;

    @Test
    @DisplayName("Test valid request functionality")
    void givenValidRequestWhenValidateThenNoErrors() {
        // given
        TrainerSummaryRequest request = EntityTestData.getValidTrainerSummaryRequest();

        errors = new BeanPropertyBindingResult(request, "trainerSummaryRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @ParameterizedTest
    @DisplayName("Test null fields validation functionality")
    @MethodSource("provideTestData")
    void givenNullRequestWhenValidateThenHasErrors(TrainerSummaryRequest request, String field, String errorCode) {
        // given
        errors = new BeanPropertyBindingResult(request, "trainerSummaryRequest");

        // when
        validator.validate(request, errors);

        // then
        FieldError fieldError = errors.getFieldError(field);

        assertThat(fieldError).isNotNull();
        assertThat(fieldError.getCode()).isEqualTo(errorCode);
    }

    static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of(EntityTestData.getValidTrainerSummaryRequest().username(null), "username", "USERNAME_CANNOT_BE_NULL"),
                Arguments.of(EntityTestData.getValidTrainerSummaryRequest().firstName(null), "firstName", "FIRSTNAME_CANNOT_BE_NULL"),
                Arguments.of(EntityTestData.getValidTrainerSummaryRequest().lastName(null), "lastName", "LASTNAME_CANNOT_BE_NULL"),
                Arguments.of(EntityTestData.getValidTrainerSummaryRequest().trainingDate(null), "trainingDate", "TRAINING_DATE_CANNOT_BE_NULL"),
                Arguments.of(EntityTestData.getValidTrainerSummaryRequest().trainingDuration(null), "trainingDuration", "TRAINING_DURATION_CANNOT_BE_NULL"),
                Arguments.of(EntityTestData.getValidTrainerSummaryRequest().actionType(null), "actionType", "ACTION_TYPE_CANNOT_BE_NULL"),
                Arguments.of(EntityTestData.getValidTrainerSummaryRequest().isActive(null), "isActive", "IS_ACTIVE_CANNOT_BE_NULL")
        );
    }

    @Test
    @DisplayName("Test supports functionality")
    void whenSupportsThenReturnsTrue() {
        // when
        boolean actual = validator.supports(TrainerSummaryRequest.class);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Test not supported object")
    void whenSupportsThenReturnsFalse() {
        // when
        boolean actual = validator.supports(Object.class);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Test negative training duration functionality")
    void givenNegativeTrainingDurationWhenValidateThenHasErrors() {
        // given
        TrainerSummaryRequest request = EntityTestData.getValidTrainerSummaryRequest();
        request.setTrainingDuration(-1);

        errors = new BeanPropertyBindingResult(request, "trainerSummaryRequest");

        // when
        validator.validate(request, errors);

        // then
        FieldError fieldError = errors.getFieldError("trainingDuration");

        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(fieldError).isNotNull();
        assertThat(fieldError.getCode()).isEqualTo("TRAINING_DURATION_CANNOT_BE_BELOW_ZERO");
    }
}