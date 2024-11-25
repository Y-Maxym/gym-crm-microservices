package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTrainingValidatorTest {

    private Errors errors;

    private CreateTrainingValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateTrainingValidator();
    }

    @Test
    @DisplayName("Test valid request functionality")
    void givenValidRequest_whenValidate_thenNoErrors() {
        // given
        AddTrainingRequest request = EntityTestData.getValidTrainingRequest();
        errors = new BeanPropertyBindingResult(request, "addTrainingRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test null fields functionality")
    void givenNullRequest_whenValidate_thenHasErrors() {
        // given
        AddTrainingRequest request = EntityTestData.getInvalidTrainingRequest();
        errors = new BeanPropertyBindingResult(request, "addTrainingRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(5);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .contains("trainee.username.empty.error",
                        "trainer.username.empty.error",
                        "training.name.empty.error",
                        "training.date.empty.error",
                        "training.duration.empty.error");
    }

    @Test
    @DisplayName("Test supports Trainee request functionality")
    void whenSupports_thenReturnsTrue() {
        // when
        boolean actual = validator.supports(AddTrainingRequest.class);

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
        AddTrainingRequest request = EntityTestData.getValidTrainingRequest();
        request.setTraineeUsername("long".repeat(100));
        request.setTrainerUsername("long".repeat(100));
        request.setTrainingName("long".repeat(100));
        errors = new BeanPropertyBindingResult(request, "addTrainingRequest");

        // when
        validator.validate(request, errors);

        // then
        assertThat(errors.getErrorCount()).isEqualTo(3);
        assertThat(errors.getFieldErrors()).extracting(ObjectError::getCode)
                .containsExactly("trainee.username.length.error", "trainer.username.length.error", "training.name.length.error");
    }

}