package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class UpdateTraineeValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return UpdateTraineeProfileRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        UpdateTraineeProfileRequest request = (UpdateTraineeProfileRequest) target;

        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        Boolean isActive = request.getIsActive();

        if (isBlank(firstName)) {
            errors.rejectValue("firstName", "first.name.empty.error", "First name is required");
        }
        if (isBlank(lastName)) {
            errors.rejectValue("lastName", "last.name.empty.error", "Last name is required");
        }
        if (isNull(isActive)) {
            errors.rejectValue("isActive", "isActive.empty.error", "Active is required");
        }
        if (nonNull(firstName) && firstName.length() > 50) {
            errors.rejectValue("firstName", "first.name.length.error", "First name is longer than 50 characters");
        }
        if (nonNull(lastName) && lastName.length() > 50) {
            errors.rejectValue("lastName", "last.name.length.error", "Last name is longer than 50 characters");
        }
        if (nonNull(firstName) && firstName.matches(".*\\d.*")) {
            errors.rejectValue("firstName", "first.name.digits.error", "First name contains digits");
        }
        if (nonNull(lastName) && lastName.matches(".*\\d.*")) {
            errors.rejectValue("lastName", "last.name.digits.error", "Last name contains digits");
        }
    }
}