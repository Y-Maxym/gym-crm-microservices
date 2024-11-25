package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.rest.model.GetTraineeTrainingsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TrainingTypeMapper.class})
public interface GetTraineeTrainingsMapper {

    @Mapping(target = "trainerName", source = "trainer.user.firstName")
    GetTraineeTrainingsResponse mapToGetTraineeTrainingsResponse(Training entity);
}
