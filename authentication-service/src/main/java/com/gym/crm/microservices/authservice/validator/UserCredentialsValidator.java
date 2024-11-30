package com.gym.crm.microservices.authservice.validator;

import com.gym.crm.microservices.authservice.model.UserCredentials;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class UserCredentialsValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return UserCredentials.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        UserCredentials request = (UserCredentials) target;

        String username = request.getUsername();
        String password = request.getPassword();

        if (isBlank(username)) {
            errors.rejectValue("username", "username.empty.error", "Username is required");
        }
        if (isBlank(password)) {
            errors.rejectValue("password", "password.empty.error", "Password is required");
        }
        if (nonNull(username) && username.length() > 100) {
            errors.rejectValue("username", "username.length.error", "Username is longer than 100 characters");
        }
        if (nonNull(password) && password.length() > 100) {
            errors.rejectValue("password", "password.length.error", "Password is longer than 100 characters");
        }
    }
}