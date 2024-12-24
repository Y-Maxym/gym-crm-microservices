package com.gym.crm.microservices.trainer.hours.service.utils;


import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerSummaryRequest;

import java.time.LocalDate;

public class EntityTestData {

    public static TrainerSummaryRequest getValidTrainerSummaryRequest() {
        return new TrainerSummaryRequest()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(120)
                .actionType(TrainerSummaryRequest.ActionTypeEnum.ADD);
    }
}
