package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.rest.model.TrainerTraineeProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerTraineeProfileMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    TrainerTraineeProfileResponse mapToTrainerTraineeProfileResponse(Trainee entity);
}
