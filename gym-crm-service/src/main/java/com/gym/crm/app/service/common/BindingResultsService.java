package com.gym.crm.app.service.common;

import com.gym.crm.app.exception.ApplicationException;
import com.gym.crm.app.rest.exception.FieldError;
import com.gym.crm.app.util.function.TriFunction;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public class BindingResultsService {

    public void handle(BindingResult bindingResult, TriFunction<String, Integer, List<FieldError>, ? extends ApplicationException> exception, String message, Integer code) {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::new)
                    .toList();

            throw exception.apply(message, code, errors);
        }
    }
}
