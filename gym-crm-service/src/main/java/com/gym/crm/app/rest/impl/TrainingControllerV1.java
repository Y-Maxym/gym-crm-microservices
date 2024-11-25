package com.gym.crm.app.rest.impl;

import com.gym.crm.app.facade.ServiceFacade;
import com.gym.crm.app.rest.TrainingController;
import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.rest.model.GetTraineeTrainingsResponse;
import com.gym.crm.app.rest.model.GetTrainerTrainingsResponse;
import com.gym.crm.app.facade.validator.CreateTrainingValidator;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}/trainings")
@RequiredArgsConstructor
public class TrainingControllerV1 implements TrainingController {

    private final ServiceFacade service;
    private final CreateTrainingValidator createTrainingValidator;

    @InitBinder("addTrainingRequest")
    public void initCreateValidatorBinder(WebDataBinder binder) {
        binder.addValidators(createTrainingValidator);
    }

    @Override
    @GetMapping("/trainees/{username}")
    public ResponseEntity<List<GetTraineeTrainingsResponse>> getTraineeTrainings(@PathVariable String username,
                                                                                 @RequestParam(name = "periodFrom", required = false) LocalDate periodFrom,
                                                                                 @RequestParam(name = "periodTo", required = false) LocalDate periodTo,
                                                                                 @RequestParam(name = "profileName", required = false) String trainerName,
                                                                                 @RequestParam(name = "trainingType", required = false) String trainingType) {
        List<GetTraineeTrainingsResponse> trainings = service.getTraineeTrainingsByCriteria(username, periodFrom, periodTo, trainerName, trainingType);

        return ResponseEntity.status(HttpStatus.OK).body(trainings);
    }

    @Override
    @GetMapping("/trainers/{username}")
    public ResponseEntity<List<GetTrainerTrainingsResponse>> getTrainerTrainings(@PathVariable String username,
                                                                                 @RequestParam(name = "periodFrom", required = false) LocalDate periodFrom,
                                                                                 @RequestParam(name = "periodTo", required = false) LocalDate periodTo,
                                                                                 @RequestParam(name = "profileName", required = false) String trainerName) {
        List<GetTrainerTrainingsResponse> trainings = service.getTrainerTrainingsByCriteria(username, periodFrom, periodTo, trainerName);

        return ResponseEntity.status(HttpStatus.OK).body(trainings);
    }

    @Override
    @PostMapping
    public ResponseEntity<?> createTraining(@RequestBody @Validated AddTrainingRequest trainingRequest,
                                            BindingResult bindingResult) {
        service.addTraining(trainingRequest, bindingResult);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
