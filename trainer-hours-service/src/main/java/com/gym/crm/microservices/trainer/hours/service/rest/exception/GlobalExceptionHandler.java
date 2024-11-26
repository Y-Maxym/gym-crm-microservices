package com.gym.crm.microservices.trainer.hours.service.rest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final int NOT_FOUND_CODE = 404;

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(DataNotFoundException e) {
        ErrorResponse error = new ErrorResponse(NOT_FOUND_CODE, e.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(error);
    }
}
