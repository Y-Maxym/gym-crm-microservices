package com.gym.crm.app.rest.impl;

import com.gym.crm.app.entity.User;
import com.gym.crm.app.facade.ServiceFacade;
import com.gym.crm.app.facade.validator.CreateTraineeValidator;
import com.gym.crm.app.facade.validator.UpdateTraineeValidator;
import com.gym.crm.app.rest.TraineeController;
import com.gym.crm.app.rest.model.GetTraineeProfileResponse;
import com.gym.crm.app.rest.model.TraineeCreateRequest;
import com.gym.crm.app.rest.model.TrainerProfileOnlyUsername;
import com.gym.crm.app.rest.model.TrainerProfileWithUsername;
import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import com.gym.crm.app.rest.model.UpdateTraineeProfileResponse;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.security.AuthenticatedUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/trainees")
@RequiredArgsConstructor
public class TraineeControllerV1 implements TraineeController {

    private final ServiceFacade service;
    private final CreateTraineeValidator createValidator;
    private final UpdateTraineeValidator updateValidator;

    @InitBinder("traineeCreateRequest")
    public void initCreateValidatorBinder(WebDataBinder binder) {
        binder.addValidators(createValidator);
    }

    @InitBinder("updateTraineeProfileRequest")
    public void initUpdateValidatorBinder(WebDataBinder binder) {
        binder.addValidators(updateValidator);
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<UserCredentials> traineeRegister(@RequestBody @Validated TraineeCreateRequest request,
                                                           BindingResult bindingResult) {
        UserCredentials profile = service.createTraineeProfile(request, bindingResult);

        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @Override
    @GetMapping("/{username}")
    public ResponseEntity<GetTraineeProfileResponse> getTraineeProfile(@PathVariable String username) {
        GetTraineeProfileResponse trainee = service.findTraineeProfileByUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(trainee);
    }

    @Override
    @PutMapping("/{username}")
    public ResponseEntity<UpdateTraineeProfileResponse> updateTraineeProfile(@PathVariable String username,
                                                                             @RequestBody @Validated UpdateTraineeProfileRequest request,
                                                                             BindingResult bindingResult) {
        User sessionUser = AuthenticatedUserUtil.getAuthenticatedUser();
        UpdateTraineeProfileResponse profile = service.updateTraineeProfile(username, request, bindingResult, sessionUser);

        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @Override
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteTraineeProfile(@PathVariable String username) {
        User sessionUser = AuthenticatedUserUtil.getAuthenticatedUser();
        service.deleteTraineeProfileByUsername(username, sessionUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerProfileWithUsername>> updateTraineeTrainerList(@PathVariable String username,
                                                                                     @RequestBody List<TrainerProfileOnlyUsername> request) {
        User sessionUser = AuthenticatedUserUtil.getAuthenticatedUser();
        List<TrainerProfileWithUsername> trainers = service.updateTraineesTrainerList(username, request, sessionUser);

        return ResponseEntity.status(HttpStatus.OK).body(trainers);
    }
}
