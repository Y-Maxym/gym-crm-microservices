package com.gym.crm.app.facade.mapper;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.entity.User;
import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import com.gym.crm.app.rest.model.UpdateTraineeProfileResponse;
import com.gym.crm.app.service.common.UserProfileService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring", uses = {UpdateUserProfileMapper.class, TrainingTypeMapper.class, TrainerProfileMapper.class})
public abstract class UpdateTraineeProfileMapper {

    @Autowired
    private UserProfileService userProfileService;

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainersList", source = "trainers")
    public abstract UpdateTraineeProfileResponse mapToUpdateTraineeProfileResponse(Trainee entity);

    public Trainee updateTraineeProfileFromDto(UpdateTraineeProfileRequest dto, Trainee entity) {
        Trainee.TraineeBuilder builder = entity.toBuilder();
        ofNullable(dto.getDateOfBirth()).ifPresent(builder::dateOfBirth);
        ofNullable(dto.getAddress()).ifPresent(builder::address);
        builder.user(updateUserFromTraineeDto(dto, entity.getUser()));

        return builder.build();
    }

    private User updateUserFromTraineeDto(UpdateTraineeProfileRequest dto, User entity) {
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
