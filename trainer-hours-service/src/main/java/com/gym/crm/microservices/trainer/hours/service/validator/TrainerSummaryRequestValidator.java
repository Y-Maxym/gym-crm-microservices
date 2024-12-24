package com.gym.crm.microservices.trainer.hours.service.validator;

import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerSummaryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TrainerSummaryRequestValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return TrainerSummaryRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        TrainerSummaryRequest request = (TrainerSummaryRequest) target;

        checkIfNull(request.getUsername(), "username", errors, "USERNAME_CANNOT_BE_NULL", "Username cannot be null");
        checkIfNull(request.getFirstName(), "firstName", errors, "FIRSTNAME_CANNOT_BE_NULL", "First name cannot be null");
        checkIfNull(request.getLastName(), "lastName", errors, "LASTNAME_CANNOT_BE_NULL", "Last name cannot be null");
        checkIfNull(request.getTrainingDate(), "trainingDate", errors, "TRAINING_DATE_CANNOT_BE_NULL", "Training date cannot be null");
        checkIfNull(request.getActionType(), "actionType", errors, "ACTION_TYPE_CANNOT_BE_NULL", "Action type cannot be null");
        checkIfNull(request.getIsActive(), "isActive", errors, "IS_ACTIVE_CANNOT_BE_NULL", "Is active cannot be null");

        checkTrainingDuration(request.getTrainingDuration(), errors);
    }

    private void checkIfNull(Object value, String fieldName, Errors errors, String errorCode, String defaultMessage) {
        if (isNull(value)) {
            errors.rejectValue(fieldName, errorCode, defaultMessage);

            log.error(defaultMessage);
        }
    }

    private void checkTrainingDuration(Integer trainingDuration, Errors errors) {
        checkIfNull(trainingDuration, "trainingDuration", errors, "TRAINING_DURATION_CANNOT_BE_NULL", "Training duration cannot be null");

        if (nonNull(trainingDuration) && trainingDuration < 0) {
            errors.rejectValue("trainingDuration", "TRAINING_DURATION_CANNOT_BE_BELOW_ZERO", "Training duration cannot be negative");

            log.error("Training duration cannot be negative");
        }
    }
}
