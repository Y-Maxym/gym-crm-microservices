package com.gym.crm.app.service.spectification;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.service.search.TrainerTrainingSearchFilter;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class TrainerTrainingSpecification extends TrainingSpecification {

    public static Specification<Training> findByCriteria(TrainerTrainingSearchFilter searchFilter) {
        return Specification.where(trainerUsernamePredicate(searchFilter.getUsername()))
                .and(dateRangePredicate(searchFilter.getFrom(), searchFilter.getTo()))
                .and(traineeNamePredicate(searchFilter.getProfileName()));
    }

    private static Specification<Training> trainerUsernamePredicate(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("trainer").get("user").get("username"), username);
    }

    private static Specification<Training> traineeNamePredicate(@Nullable String trainerName) {
        return (root, query, criteriaBuilder) -> {
            if (isNotBlank(trainerName)) {
                return criteriaBuilder.equal(root.get("trainee").get("user").get("firstName"), trainerName);
            }

            return criteriaBuilder.conjunction();
        };
    }
}
