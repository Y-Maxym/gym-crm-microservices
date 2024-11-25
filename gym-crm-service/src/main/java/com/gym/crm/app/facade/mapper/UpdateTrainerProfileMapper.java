package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainer;
import com.gym.crm.app.entity.User;
import com.gym.crm.app.rest.model.UpdateTrainerProfileRequest;
import com.gym.crm.app.rest.model.UpdateTrainerProfileResponse;
import com.gym.crm.app.service.common.UserProfileService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UpdateUserProfileMapper.class, TrainerTraineeProfileMapper.class, TrainingTypeMapper.class})
public abstract class UpdateTrainerProfileMapper {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private TrainingTypeMapper trainingTypeMapper;

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "traineesList", source = "trainees")
    public abstract UpdateTrainerProfileResponse mapToUpdateTrainerProfileResponse(Trainer entity);

    public Trainer updateTraineeProfileFromDto(UpdateTrainerProfileRequest dto, Trainer entity) {
        return entity.toBuilder()
                .specialization(trainingTypeMapper.mapToTrainingType(dto.getSpecialization()))
                .user(updateUserFromTrainerDto(dto, entity.getUser()))
                .build();
    }

    private User updateUserFromTrainerDto(UpdateTrainerProfileRequest dto, User entity) {
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String username = entity.getUsername();

        if (!firstName.equals(dto.getFirstName()) || !lastName.equals(dto.getLastName())) {
            username = userProfileService.generateUsername(dto.getFirstName(), dto.getLastName());
        }

        return entity.toBuilder()
                .username(username)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .isActive(dto.getIsActive())
                .build();
    }
}
