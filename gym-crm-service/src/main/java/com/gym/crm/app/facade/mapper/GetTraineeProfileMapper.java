package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.rest.model.GetTraineeProfileResponse;
import com.gym.crm.app.rest.model.TraineeCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CreateUserProfileMapper.class, TrainingTypeMapper.class, TrainerProfileMapper.class})
public interface GetTraineeProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "user", source = ".")
    Trainee mapToTrainee(TraineeCreateRequest dto);

    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainersList", source = "trainers")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    GetTraineeProfileResponse mapToGetTraineeProfileResponse(Trainee entity);
}
