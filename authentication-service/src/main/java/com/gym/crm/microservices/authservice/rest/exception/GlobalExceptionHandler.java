package com.gym.crm.microservices.authservice.rest.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.gym.crm.microservices.authservice.exception.AccessTokenException;
import com.gym.crm.microservices.authservice.exception.AuthenticationException;
import com.gym.crm.microservices.authservice.exception.EntityPersistException;
import com.gym.crm.microservices.authservice.exception.EntityValidationException;
import com.gym.crm.microservices.authservice.exception.PasswordOperationException;
import com.gym.crm.microservices.authservice.exception.ValidationError;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.GENERAL_ERROR;
import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.INVALID_FIELD_FORMAT;
import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.UNRECOGNIZED_FIELD;
import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.VALUE_INSTANTIATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessTokenException.class)
    public ResponseEntity<ErrorResponse> handleException(AccessTokenException e) {
        ErrorResponse error = new ErrorResponse(e.getCode(), e.getMessage());

        return ResponseEntity.status(FORBIDDEN).body(error);
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

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException e) {
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
