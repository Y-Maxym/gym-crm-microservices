package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.rest.model.GetTrainerProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TrainerTraineeProfileMapper.class, TrainingTypeMapper.class})
public interface GetTrainerProfileMapper {

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "traineesList", source = "trainees")
    @Mapping(target = "isActive", source = "user.active")
    GetTrainerProfileResponse mapToGetTrainerProfileResponse(Trainer entity);

}
