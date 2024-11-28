package com.gym.crm.app.service;

import  com.gym.crm.app.entity.Training;
import com.gym.crm.app.service.common.dto.TrainerSummaryRequest;
import com.gym.crm.app.service.search.TrainingSearchFilter;

import java.util.List;

public interface TrainingService {

    Training findById(Long id);

    List<Training> findAll(TrainingSearchFilter filter);

    void save(Training training);

    void notifyTrainerSummaryService(Training training, TrainerSummaryRequest.ActionType operation);

}
