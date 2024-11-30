package com.gym.crm.app.service.impl;

import com.gym.crm.app.client.TrainerHoursClient;
import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.exception.ServiceUnavailableException;
import com.gym.crm.app.logging.MessageHelper;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.EntityValidator;
import com.gym.crm.app.service.common.dto.TrainerSummaryRequest;
import com.gym.crm.app.service.search.TrainingSearchFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gym.crm.app.rest.exception.ErrorCode.TRAINING_WITH_ID_NOT_FOUND;
import static com.gym.crm.app.util.Constants.ERROR_TRAINING_WITH_ID_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final int TRAINER_HOURS_SERVICE_UNAVAILABLE_CODE = 8082;
    private static final String TRAINER_HOURS_SERVICE_UNAVAILABLE_MESSAGE = "Trainer Hours Service is currently unavailable. Please try again later";
    private static final String TRAINER_HOURS_SERVICE_UNAVAILABLE_ERROR = "Trainer hours service unavailable";

    private final MessageHelper messageHelper;
    private final TrainingRepository repository;
    private final EntityValidator validator;
    private final TrainerHoursClient client;

    @Override
    @Transactional(readOnly = true)
    public Training findById(Long id) {
        validator.checkId(id);

        return repository.findById(id)
                .orElseThrow(() -> new EntityValidationException(messageHelper.getMessage(ERROR_TRAINING_WITH_ID_NOT_FOUND, id), TRAINING_WITH_ID_NOT_FOUND.getCode()));
    }

    @Override
    public List<Training> findAll(TrainingSearchFilter filter) {
        Specification<Training> specification = filter.toSpecification();

        return repository.findAll(specification);
    }

    @Override
    @Transactional
    public void save(Training training) {
        validator.checkEntity(training);

        training = repository.save(training);

        notifyTrainerSummaryService(training, TrainerSummaryRequest.ActionType.ADD);
    }

    @Override
    public void notifyTrainerSummaryService(Training training, TrainerSummaryRequest.ActionType operation) {
        Trainer trainer = training.getTrainer();
        TrainerSummaryRequest request = TrainerSummaryRequest.builder()
                .username(trainer.getUser().getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .isActive(trainer.getUser().isActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(operation)
                .build();

        ResponseEntity<?> response = client.postTrainerSummary(request);

        if (response.getStatusCode().is5xxServerError()) {
            log.error(TRAINER_HOURS_SERVICE_UNAVAILABLE_ERROR);

            throw new ServiceUnavailableException(TRAINER_HOURS_SERVICE_UNAVAILABLE_MESSAGE, TRAINER_HOURS_SERVICE_UNAVAILABLE_CODE);
        }
    }
}
