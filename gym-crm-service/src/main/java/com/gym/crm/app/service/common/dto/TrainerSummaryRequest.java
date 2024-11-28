package com.gym.crm.app.service.common.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
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

    public enum ActionType {
        ADD, DELETE
    }
}
