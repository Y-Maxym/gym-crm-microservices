package com.gym.crm.app.service.impl;

import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.EntityValidator;
import com.gym.crm.app.service.search.TrainerTrainingSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gym.crm.app.rest.exception.ErrorCode.TRAINER_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINER_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_TRAINER_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_TRAINER_WITH_USERNAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final MessageHelper messageHelper;
    private final TrainerRepository repository;
    private final EntityValidator validator;
    private final TrainingService trainingService;

    @Override
    @Transactional(readOnly = true)
    public Trainer findById(Long id) {
        validator.checkId(id);

        return repository.findById(id)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_TRAINER_WITH_ID_NOT_FOUND, id), TRAINER_WITH_ID_NOT_FOUND.getCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(String username) {
        return repository.findByUserUsername(username)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_TRAINER_WITH_USERNAME_NOT_FOUND, username), TRAINER_WITH_USERNAME_NOT_FOUND.getCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingByCriteria(TrainerTrainingSearchFilter searchFilter) {
        validator.checkIfTrainerExist(searchFilter.getUsername());

        return trainingService.findAll(searchFilter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getTrainersNotAssignedByTraineeUsername(String username) {
        validator.checkEntity(username);

        return repository.getTrainersNotAssignedByTraineeUsername(username);
    }

    @Override
    @Transactional
    public void save(Trainer trainer) {
        validator.checkEntity(trainer);

        repository.save(trainer);
    }

    @Override
    @Transactional
    public Trainer update(Trainer trainer) {
        validator.checkEntity(trainer);
        validator.checkId(trainer.getId());

        return repository.save(trainer);
    }
}
