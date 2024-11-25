package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.rest.model.TrainerProfileWithUsername;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TrainingTypeMapper.class})
public interface TrainerProfileMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    TrainerProfileWithUsername mapToTrainerProfileWithUsername(Trainer entity);
}
