package com.gym.crm.app.service.search;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.service.spectification.TrainerTrainingSpecification;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@SuperBuilder
public class TrainerTrainingSearchFilter extends TrainingSearchFilter {

    @Override
    public Specification<Training> toSpecification() {
        return TrainerTrainingSpecification.findByCriteria(this);
    }
}
