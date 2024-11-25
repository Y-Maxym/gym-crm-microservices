package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.rest.model.GetTrainerTrainingsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TrainingTypeMapper.class)
public interface GetTrainerTrainingsMapper {

    @Mapping(target = "traineeName", source = "trainee.user.firstName")
    GetTrainerTrainingsResponse mapToGetTrainerTrainingsResponse(Training entity);
}
