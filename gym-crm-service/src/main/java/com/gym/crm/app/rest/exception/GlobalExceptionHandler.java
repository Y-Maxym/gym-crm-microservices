package com.gym.crm.app.rest.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.gym.crm.app.exception.AuthenticationException;
import com.gym.crm.app.exception.EntityPersistException;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.exception.PasswordOperationException;
import com.gym.crm.app.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.gym.crm.app.rest.exception.ErrorCode.GENERAL_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.INVALID_FIELD_FORMAT;
import static com.gym.crm.app.rest.exception.ErrorCode.UNRECOGNIZED_FIELD;
import static com.gym.crm.app.rest.exception.ErrorCode.VALUE_INSTANTIATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(ServiceUnavailableException e) {
        ErrorResponse error = new ErrorResponse(e.getCode(), e.getMessage());
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityValidationException e) {
        ErrorResponse error = new ErrorResponse(e.getCode(), e.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleException(AuthenticationException e) {
        ErrorResponse error = new ErrorResponse(e.getCode(), e.getMessage());

        return ResponseEntity.status(UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(EntityPersistException.class)
    public ResponseEntity<ValidationError> handleException(EntityPersistException e) {
        ValidationError error = new ValidationError(e.getCode(), e.getMessage(), e.getErrors());

        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PasswordOperationException.class)
    public ResponseEntity<ErrorResponse> handleException(PasswordOperationException e) {
        ErrorResponse error = new ErrorResponse(e.getCode(), e.getMessage());
        log.error(e.getMessage(), e);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return handleHttpMessageNotReadableException(e);
    }

    private ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = e.getMessage();
        int code = GENERAL_ERROR.getCode();
        Throwable cause = e.getCause();

        if (cause instanceof InvalidFormatException) {
            message = "Invalid field format";
            code = INVALID_FIELD_FORMAT.getCode();
        } else if (cause instanceof ValueInstantiationException && cause.getCause() != null) {
            message = cause.getCause().getMessage();
            code = VALUE_INSTANTIATION.getCode();
        } else if (cause instanceof UnrecognizedPropertyException ex) {
            message = "Unrecognized field %s".formatted(ex.getPropertyName());
            code = UNRECOGNIZED_FIELD.getCode();
        }

        ErrorResponse error = new ErrorResponse(code, message);
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }
}
