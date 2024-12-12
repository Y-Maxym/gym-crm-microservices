package com.gym.crm.microservices.trainer.hours.service.utils;


import com.gym.crm.microservices.trainer.hours.service.model.TrainerSummaryRequest;

import java.time.LocalDate;

public class EntityTestData {

    public static TrainerSummaryRequest getValidTrainerSummaryRequest() {
        return new TrainerSummaryRequest()
                .firstName("firstName")
                .lastName("lastName")
                .username("username")
                .isActive(true)
                .actionType(TrainerSummaryRequest.ActionTypeEnum.ADD)
                .trainingDuration(2)
                .trainingDate(LocalDate.now());
    }
}
