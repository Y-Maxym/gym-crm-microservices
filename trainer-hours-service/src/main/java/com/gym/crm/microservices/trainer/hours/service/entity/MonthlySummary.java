package com.gym.crm.microservices.trainer.hours.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "month_summary")
public class MonthlySummary {

    @Id
    private String id;

    private Integer month;

    @Field(name = "total_training_duration")
    private Integer totalTrainingDuration;
}
