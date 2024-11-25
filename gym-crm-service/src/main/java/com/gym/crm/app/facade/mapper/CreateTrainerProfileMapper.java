package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CreateUserProfileMapper.class, TrainingTypeMapper.class})
public interface CreateTrainerProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "user", source = ".")
    Trainer mapToTrainer(TrainerCreateRequest dto);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    UserCredentials mapToUserCredentials(Trainer entity);
}
