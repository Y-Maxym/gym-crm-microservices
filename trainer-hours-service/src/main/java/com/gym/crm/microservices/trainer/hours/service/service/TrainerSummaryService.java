package com.gym.crm.microservices.trainer.hours.service.service;

import com.gym.crm.microservices.trainer.hours.service.dto.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.dto.TrainerWorkloadResponse;

public interface TrainerSummaryService {

    void sumTrainerSummary(TrainerSummaryRequest request);

    TrainerWorkloadResponse getTrainerWorkload(String username, Integer year, Integer month);
}
