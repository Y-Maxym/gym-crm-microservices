package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.Training;
import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {TrainerProfileMapper.class, TrainingTypeMapper.class})
@Setter(onMethod_ = {@Autowired})
public abstract class AddTrainingMapper {

    protected TrainerService trainerService;
    protected TraineeService traineeService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", expression = "java(mapTrainer(dto.getTrainerUsername()))")
    @Mapping(target = "trainee", expression = "java(mapTrainee(dto.getTraineeUsername()))")
    @Mapping(target = "trainingType", ignore = true)
    public abstract Training mapToTraining(AddTrainingRequest dto);

    protected Trainer mapTrainer(String trainerUsername) {
        return trainerService.findByUsername(trainerUsername);
    }

    protected Trainee mapTrainee(String traineeUsername) {
        return traineeService.findByUsername(traineeUsername);
    }
}
