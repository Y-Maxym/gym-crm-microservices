package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.AddTrainingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class CreateTrainingValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return AddTrainingRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        AddTrainingRequest request = (AddTrainingRequest) target;

        String traineeUsername = request.getTraineeUsername();
        String trainerUsername = request.getTrainerUsername();
        String trainingName = request.getTrainingName();
        LocalDate trainingDate = request.getTrainingDate();
        Integer trainingDuration = request.getTrainingDuration();

        if (isBlank(traineeUsername)) {
            errors.rejectValue("traineeUsername", "trainee.username.empty.error", "Trainee username is required");
        }
        if (isBlank(trainerUsername)) {
            errors.rejectValue("trainerUsername", "trainer.username.empty.error", "Trainer username is required");
        }
        if (isBlank(trainingName)) {
            errors.rejectValue("trainingName", "training.name.empty.error", "Training name is required");
        }
        if (isNull(trainingDate)) {
            errors.rejectValue("trainingDate", "training.date.empty.error", "Training date is required");
        }
        if (isNull(trainingDuration)) {
            errors.rejectValue("trainingDuration", "training.duration.empty.error", "Training duration is required");
        }
        if (nonNull(traineeUsername) && traineeUsername.length() > 100) {
            errors.rejectValue("traineeUsername", "trainee.username.length.error", "Trainee username is longer than 100 characters");
        }
        if (nonNull(trainerUsername) && trainerUsername.length() > 100) {
            errors.rejectValue("trainerUsername", "trainer.username.length.error", "Trainer username is longer than 100 characters");
        }
        if (nonNull(trainingName) && trainingName.length() > 100) {
            errors.rejectValue("trainingName", "training.name.length.error", "Training name is longer than 100 characters");
        }
    }
}