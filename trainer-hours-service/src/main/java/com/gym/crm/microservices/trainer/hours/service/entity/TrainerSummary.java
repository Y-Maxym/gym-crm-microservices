package com.gym.crm.microservices.trainer.hours.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainer_summary")
@CompoundIndex(def = "{'first_name': 1, 'last_name': 1}")
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
    @Field(name = "yearly_summaries")
    private List<YearlySummary> yearlySummaries = new ArrayList<>();
}
