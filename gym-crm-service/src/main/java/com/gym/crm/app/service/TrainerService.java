package com.gym.crm.app.service;

import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.service.search.TrainerTrainingSearchFilter;

import java.util.List;

public interface TrainerService {

    Trainer findById(Long id);

    Trainer findByUsername(String username);

    List<Training> findTrainingByCriteria(TrainerTrainingSearchFilter searchFilter);

    List<Trainer> getTrainersNotAssignedByTraineeUsername(String username);

    void save(Trainer trainer);

    Trainer update(Trainer trainer);
}
