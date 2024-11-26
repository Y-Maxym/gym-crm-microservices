package com.gym.crm.microservices.trainer.hours.service.rest;

import com.gym.crm.microservices.trainer.hours.service.dto.TrainerSummaryRequest;
import org.springframework.http.ResponseEntity;

public interface TrainerSummaryController {

    ResponseEntity<?> sumWorkload(TrainerSummaryRequest request);

    ResponseEntity<?> getTrainerWorkload(String username,
                                         Integer year,
                                         Integer month);

}
