package com.gym.crm.microservices.trainer.hours.service.service;

import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerWorkloadResponse;

public interface TrainerSummaryService {

    void sumTrainerSummary(TrainerSummaryRequest request);

    TrainerWorkloadResponse getTrainerWorkload(String username, Integer year, Integer month);
}
