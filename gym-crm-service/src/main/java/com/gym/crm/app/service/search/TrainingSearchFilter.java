package com.gym.crm.app.service.search;

import com.gym.crm.app.entity.Training;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Getter
@SuperBuilder(toBuilder = true)
public abstract class TrainingSearchFilter {

    private String username;
    private LocalDate from;
    private LocalDate to;
    private String profileName;

    public abstract Specification<Training> toSpecification();
}
