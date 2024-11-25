package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.rest.model.TraineeCreateRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CreateUserProfileMapper.class)
public interface CreateTraineeProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "user", source = ".")
    Trainee mapToTrainee(TraineeCreateRequest dto);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    UserCredentials mapToUserCredentials(Trainee entity);
}
