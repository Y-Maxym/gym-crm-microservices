package com.gym.crm.app.service.common;

import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.gym.crm.app.rest.exception.ErrorCode.NULL_ENTITY;
import static com.gym.crm.app.rest.exception.ErrorCode.NULL_ENTITY_ID;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINEE_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.rest.exception.ErrorCode.ZERO_ENTITY_ID;
import static com.gym.crm.app.util.Constants.ERROR_ENTITY_CANNOT_BE_NULL;
import static com.gym.crm.app.util.Constants.ERROR_ENTITY_ID_CANNOT_BE_NULL;
import static com.gym.crm.app.util.Constants.ERROR_ENTITY_ID_CANNOT_BE_ZERO;
import static com.gym.crm.app.util.Constants.ERROR_TRAINEE_WITH_USERNAME_NOT_FOUND;
import static java.util.Objects.isNull;


@Component
@RequiredArgsConstructor
public class EntityValidator {

    private final MessageHelper messageHelper;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public void checkId(Long id) {
        if (isNull(id)) {
            throw new EntityValidationException(messageHelper.getMessage(ERROR_ENTITY_ID_CANNOT_BE_NULL), NULL_ENTITY_ID.getCode());
        }

        if (id <= 0) {
            throw new EntityValidationException(messageHelper.getMessage(ERROR_ENTITY_ID_CANNOT_BE_ZERO), ZERO_ENTITY_ID.getCode());
        }
    }

    public void checkEntity(Object entity) {
        if (isNull(entity)) {
            throw new EntityValidationException(messageHelper.getMessage(ERROR_ENTITY_CANNOT_BE_NULL), NULL_ENTITY.getCode());
        }
    }

    @Transactional(readOnly = true)
    public void checkIfTraineeExist(String username) {
        traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_TRAINEE_WITH_USERNAME_NOT_FOUND, username), TRAINEE_WITH_USERNAME_NOT_FOUND.getCode()));
    }

    @Transactional(readOnly = true)
    public void checkIfTrainerExist(String username) {
        trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_TRAINEE_WITH_USERNAME_NOT_FOUND, username), TRAINEE_WITH_USERNAME_NOT_FOUND.getCode()));
    }
}
