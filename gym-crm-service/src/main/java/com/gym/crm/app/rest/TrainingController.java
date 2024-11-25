package com.gym.crm.app.rest;

import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.rest.model.ErrorResponse;
import com.gym.crm.app.rest.model.GetTraineeTrainingsResponse;
import com.gym.crm.app.rest.model.GetTrainerTrainingsResponse;
import com.gym.crm.app.rest.model.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Trainings", description = "Training endpoints")
public interface TrainingController {

    @Operation(summary = "Get trainee trainings list", description = "Retrieve the list of trainings for a specific trainee")
    @Parameter(name = "username", description = "The username of the trainee", required = true)
    @Parameter(name = "periodFrom", description = "Start date of the training period")
    @Parameter(name = "periodTo", description = "End date of the training period")
    @Parameter(name = "trainerName", description = "Filter trainings by trainer's name")
    @Parameter(name = "trainingType", description = "Filter trainings by type")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of trainee trainings", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = GetTraineeTrainingsResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<GetTraineeTrainingsResponse>> getTraineeTrainings(String username,
                                                                          LocalDate periodFrom,
                                                                          LocalDate periodTo,
                                                                          String trainerName,
                                                                          String trainingType);

    @Operation(summary = "Get trainer trainings list", description = "Retrieve the list of trainings for a specific trainer")
    @Parameter(name = "username", description = "The username of the trainer", required = true)
    @Parameter(name = "periodFrom", description = "Start date of the training period")
    @Parameter(name = "periodTo", description = "End date of the training period")
    @Parameter(name = "traineeName", description = "Filter trainings by trainee's name")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of trainer trainings", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = GetTrainerTrainingsResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<GetTrainerTrainingsResponse>> getTrainerTrainings(String username,
                                                                          LocalDate periodFrom,
                                                                          LocalDate periodTo,
                                                                          String trainerName);

    @Operation(summary = "Add training", description = "Create a new training session")
    @RequestBody(description = "Request to create a new training", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddTrainingRequest.class)))
    @ApiResponse(responseCode = "200", description = "Training successfully added")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> createTraining(AddTrainingRequest trainingRequest,
                                     BindingResult bindingResult);
}
