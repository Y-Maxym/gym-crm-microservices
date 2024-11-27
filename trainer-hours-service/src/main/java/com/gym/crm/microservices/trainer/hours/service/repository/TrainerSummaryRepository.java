package com.gym.crm.microservices.trainer.hours.service.repository;

import com.gym.crm.microservices.trainer.hours.service.entity.TrainerSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerSummaryRepository extends JpaRepository<TrainerSummary, Long> {

    Optional<TrainerSummary> findByUsername(String username);

    @Query("""
            SELECT ms.totalTrainingDuration
            FROM TrainerSummary ts
            JOIN ts.yearlySummaries ys
            JOIN ys.monthlySummaries ms
            WHERE ts.username = :username
            AND ys.year = :year
            AND ms.month = :month
            """)
    Integer findWorkloadByParams(@Param("username") String username,
                                 @Param("year") Integer year,
                                 @Param("month") Integer month);
}
