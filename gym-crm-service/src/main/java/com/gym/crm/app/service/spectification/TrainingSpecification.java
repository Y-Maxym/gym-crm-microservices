package com.gym.crm.app.service.spectification;

import com.gym.crm.app.entity.Training;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static java.util.Objects.nonNull;

public class TrainingSpecification {

    protected static Specification<Training> dateRangePredicate(@Nullable LocalDate from, @Nullable LocalDate to) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (nonNull(from)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("trainingDate"), from));
            }
            if (nonNull(to)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("trainingDate"), to));
            }

            return predicate;
        };
    }
}
