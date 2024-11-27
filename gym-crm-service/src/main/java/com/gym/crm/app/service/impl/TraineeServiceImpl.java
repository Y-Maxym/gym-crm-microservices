package com.gym.crm.app.service.impl;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.rest.model.TrainerSummaryRequest;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.EntityValidator;
import com.gym.crm.app.service.search.TraineeTrainingSearchFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gym.crm.app.rest.exception.ErrorCode.TRAINEE_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINEE_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_TRAINEE_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_TRAINEE_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.util.Constants.WARN_TRAINEE_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.WARN_TRAINEE_WITH_USERNAME_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final MessageHelper messageHelper;
    private final TraineeRepository repository;
    private final EntityValidator validator;
    private final TrainingService trainingService;

    @Override
    @Transactional(readOnly = true)
    public Trainee findById(Long id) {
        validator.checkId(id);

        return repository.findById(id)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_TRAINEE_WITH_ID_NOT_FOUND, id), TRAINEE_WITH_ID_NOT_FOUND.getCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee findByUsername(String username) {
        return repository.findByUserUsername(username)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_TRAINEE_WITH_USERNAME_NOT_FOUND, username), TRAINEE_WITH_USERNAME_NOT_FOUND.getCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingByCriteria(TraineeTrainingSearchFilter searchFilter) {
        validator.checkIfTraineeExist(searchFilter.getUsername());

        return trainingService.findAll(searchFilter);
    }

    @Override
    @Transactional
    public void save(Trainee trainee) {
        validator.checkEntity(trainee);

        repository.save(trainee);
    }

    @Override
    @Transactional
    public Trainee update(Trainee trainee) {
        validator.checkEntity(trainee);
        validator.checkId(trainee.getId());

        return repository.save(trainee);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        validator.checkId(id);

        if (repository.findById(id).isEmpty()) {
            log.warn(messageHelper.getMessage(WARN_TRAINEE_WITH_ID_NOT_FOUND, id));
        }

        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        validator.checkEntity(username);

        repository.findByUserUsername(username)
                .ifPresentOrElse(this::deleteAndNotify,
                        () -> log.warn(messageHelper.getMessage(WARN_TRAINEE_WITH_USERNAME_NOT_FOUND, username)));
    }

    private void deleteAndNotify(Trainee trainee) {
        repository.deleteByUserUsername(trainee.getUser().getUsername());

        trainee.getTrainings()
                .forEach(training -> trainingService.notifyTrainerSummaryService(training, TrainerSummaryRequest.ActionTypeEnum.DELETE));

    }
}
