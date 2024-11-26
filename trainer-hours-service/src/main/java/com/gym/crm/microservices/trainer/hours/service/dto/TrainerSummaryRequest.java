package com.gym.crm.microservices.trainer.hours.service.dto;

import com.gym.crm.microservices.trainer.hours.service.entity.ActionType;

import java.time.LocalDate;

public record TrainerSummaryRequest
        (
                String username,
                String firstName,
                String lastName,
                boolean isActive,
                LocalDate trainingDate,
                Integer trainingDuration,
                ActionType actionType
        ) {
}
