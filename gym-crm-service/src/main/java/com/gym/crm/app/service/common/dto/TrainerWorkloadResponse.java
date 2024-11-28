package com.gym.crm.app.service.common.dto;

import lombok.Builder;

@Builder
public record TrainerWorkloadResponse
        (
                Integer workload
        ) {
}
