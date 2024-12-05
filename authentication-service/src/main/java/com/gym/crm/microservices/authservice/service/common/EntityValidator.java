package com.gym.crm.microservices.authservice.service.common;

import com.gym.crm.microservices.authservice.exception.EntityValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.NULL_ENTITY;
import static java.util.Objects.isNull;


@Component
@RequiredArgsConstructor
public class EntityValidator {

    private static final String ERROR_ENTITY_CANNOT_BE_NULL = "Entity cannot be null";

    public void checkEntity(Object entity) {
        if (isNull(entity)) {
            throw new EntityValidationException(ERROR_ENTITY_CANNOT_BE_NULL, NULL_ENTITY.getCode());
        }
    }
}
