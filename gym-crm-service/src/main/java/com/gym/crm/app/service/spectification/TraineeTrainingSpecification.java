package com.gym.crm.app.service.spectification;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.service.search.TraineeTrainingSearchFilter;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class TraineeTrainingSpecification extends TrainingSpecification {

    public static Specification<Training> findByCriteria(TraineeTrainingSearchFilter searchFilter) {
        return Specification.where(traineeUsernamePredicate(searchFilter.getUsername()))
                .and(dateRangePredicate(searchFilter.getFrom(), searchFilter.getTo()))
                .and(trainerNamePredicate(searchFilter.getProfileName()))
                .and(trainingTypePredicate(searchFilter.getTrainingType()));
    }

    private static Specification<Training> traineeUsernamePredicate(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("trainee").get("user").get("username"), username);
    }

    private static Specification<Training> trainerNamePredicate(@Nullable String trainerName) {
        return (root, query, criteriaBuilder) -> {
            if (isNotBlank(trainerName)) {
                return criteriaBuilder.equal(root.get("trainer").get("user").get("firstName"), trainerName);
            }

            return criteriaBuilder.conjunction();
        };
    }

    private static Specification<Training> trainingTypePredicate(@Nullable String trainingType) {
        return (root, query, criteriaBuilder) -> {
            if (isNotBlank(trainingType)) {
                return criteriaBuilder.equal(root.get("trainingType").get("trainingTypeName"), trainingType);
            }

            return criteriaBuilder.conjunction();
        };
    }
}
