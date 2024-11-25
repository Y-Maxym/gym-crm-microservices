package com.gym.crm.app.repository;

import com.gym.crm.app.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    @Query("FROM Trainer t WHERE t NOT IN (" +
            "SELECT t.trainers FROM Trainee t WHERE t.user.username = :username)")
    List<Trainer> getTrainersNotAssignedByTraineeUsername(String username);
}
