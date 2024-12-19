package com.gym.crm.microservices.trainer.hours.service.repository;

import com.gym.crm.microservices.trainer.hours.service.entity.TrainerSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerSummaryRepository extends MongoRepository<TrainerSummary, Long> {

    Optional<TrainerSummary> findByUsername(String username);

    @Query("{ 'username': ?0, 'yearlySummaries.year': ?1, 'yearlySummaries.monthlySummaries.month': ?2 }")
    List<TrainerSummary> findByParams(@Param("username") String username,
                                      @Param("year") Integer year,
                                      @Param("month") Integer month);
}
