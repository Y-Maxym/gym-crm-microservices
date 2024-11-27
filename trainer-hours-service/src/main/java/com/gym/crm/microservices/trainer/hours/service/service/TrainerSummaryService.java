package com.gym.crm.microservices.trainer.hours.service.service;

import com.gym.crm.microservices.trainer.hours.service.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerWorkloadResponse;

public interface TrainerSummaryService {

    void sumTrainerSummary(TrainerSummaryRequest request);

    TrainerWorkloadResponse getTrainerWorkload(String username, Integer year, Integer month);
}
