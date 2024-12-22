package com.gym.crm.microservices.trainer.hours.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainer_summary")
public class TrainerSummary {

    @Id
    private String id;

    private String username;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "last_name")
    private String lastName;

    @Field(name = "is_active")
    private boolean isActive;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<YearlySummary> yearlySummaries = new ArrayList<>();
}
