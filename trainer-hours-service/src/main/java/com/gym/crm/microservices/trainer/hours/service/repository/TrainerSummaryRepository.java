package com.gym.crm.microservices.trainer.hours.service.repository;

import com.gym.crm.microservices.trainer.hours.service.entity.TrainerSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerSummaryRepository extends JpaRepository<TrainerSummary, Long> {

    Optional<TrainerSummary> findByUsername(String username);

    @Query("""
            SELECT ms.totalTrainingDuration FROM TrainerSummary ts
            JOIN ts.yearlySummaries ys
            JOIN ys.monthlySummaries ms
            WHERE ts.username = ?1
            AND ys.year = ?2
            AND ms.month = ?3
            """)
    Integer findWorkloadByParams(String username, Integer year, Integer month);
}
