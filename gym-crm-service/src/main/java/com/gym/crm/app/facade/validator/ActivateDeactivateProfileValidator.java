package com.gym.crm.app.facade.validator;

import com.gym.crm.app.rest.model.ActivateDeactivateProfileRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static java.util.Objects.isNull;

@Component
public class ActivateDeactivateProfileValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return ActivateDeactivateProfileRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        ActivateDeactivateProfileRequest request = (ActivateDeactivateProfileRequest) target;

        Boolean isActive = request.getIsActive();

        if (isNull(isActive)) {
            errors.rejectValue("isActive", "isActive.required", "Is active required");
        }
    }
}