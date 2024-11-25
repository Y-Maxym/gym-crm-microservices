package com.gym.crm.app.rest.impl;

import com.gym.crm.app.entity.User;
import com.gym.crm.app.facade.ServiceFacade;
import com.gym.crm.app.facade.validator.CreateTrainerValidator;
import com.gym.crm.app.facade.validator.UpdateTrainerValidator;
import com.gym.crm.app.rest.TrainerController;
import com.gym.crm.app.rest.model.GetTrainerProfileResponse;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.rest.model.TrainerProfileWithUsername;
import com.gym.crm.app.rest.model.UpdateTrainerProfileRequest;
import com.gym.crm.app.rest.model.UpdateTrainerProfileResponse;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.security.AuthenticatedUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
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
@RequestMapping("${api.base-path}/trainers")
@RequiredArgsConstructor
public class TrainerControllerV1 implements TrainerController {

    private final ServiceFacade service;
    private final CreateTrainerValidator createTrainerValidator;
    private final UpdateTrainerValidator updateTrainerValidator;

    @InitBinder("trainerCreateRequest")
    public void initCreateValidatorBinder(WebDataBinder binder) {
        binder.addValidators(createTrainerValidator);
    }

    @InitBinder("updateTrainerProfileRequest")
    public void initUpdateValidatorBinder(WebDataBinder binder) {
        binder.addValidators(updateTrainerValidator);
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<UserCredentials> trainerRegister(@RequestBody @Validated TrainerCreateRequest request,
                                                           BindingResult bindingResult) {
        UserCredentials profile = service.createTrainerProfile(request, bindingResult);

        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @Override
    @GetMapping("/{username}")
    public ResponseEntity<GetTrainerProfileResponse> getTrainerProfile(@PathVariable String username) {
        GetTrainerProfileResponse trainer = service.findTrainerProfileByUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(trainer);
    }

    @Override
    @PutMapping("/{username}")
    public ResponseEntity<UpdateTrainerProfileResponse> updateTrainerProfile(@PathVariable String username,
                                                                             @RequestBody @Validated UpdateTrainerProfileRequest request,
                                                                             BindingResult bindingResult) {
        User sessionUser = AuthenticatedUserUtil.getAuthenticatedUser();
        UpdateTrainerProfileResponse profile = service.updateTrainerProfile(username, request, bindingResult, sessionUser);

        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @Override
    @GetMapping("/not-assigned/{username}")
    public ResponseEntity<List<TrainerProfileWithUsername>> getTrainersNotAssigned(@PathVariable String username) {
        List<TrainerProfileWithUsername> trainers = service.getTrainersNotAssignedByTraineeUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(trainers);
    }
}
