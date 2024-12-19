package com.gym.crm.microservices.trainer.hours.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "year_summary")
public class YearlySummary {

    @Id
    private String id;

    private Integer year;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<MonthlySummary> monthlySummaries = new ArrayList<>();
}
