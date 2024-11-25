package com.gym.crm.app.service.search;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.service.spectification.TraineeTrainingSpecification;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@Getter
@SuperBuilder
public class TraineeTrainingSearchFilter extends TrainingSearchFilter {

    private String trainingType;

    @Override
    public Specification<Training> toSpecification() {
        return TraineeTrainingSpecification.findByCriteria(this);
    }
}
