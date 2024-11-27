package com.gym.crm.microservices.trainer.hours.service.exception;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }
}
