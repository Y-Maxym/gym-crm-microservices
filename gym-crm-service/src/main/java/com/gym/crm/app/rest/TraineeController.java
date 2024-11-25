package com.gym.crm.app.rest;

import com.gym.crm.app.rest.model.ErrorResponse;
import com.gym.crm.app.rest.model.GetTraineeProfileResponse;
import com.gym.crm.app.rest.model.TraineeCreateRequest;
import com.gym.crm.app.rest.model.TrainerProfileOnlyUsername;
import com.gym.crm.app.rest.model.TrainerProfileWithUsername;
import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import com.gym.crm.app.rest.model.UpdateTraineeProfileResponse;
import com.gym.crm.app.rest.model.UserCredentials;
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

import java.util.List;

@Tag(name = "Trainees", description = "Trainee endpoints")
public interface TraineeController {

    @RequestBody(description = "Register a new trainee in the gym", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = TraineeCreateRequest.class)))
    @Operation(summary = "Register a new trainee", description = "Register a new trainee in the gym")
    @ApiResponse(responseCode = "200", description = "Successful registration", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCredentials.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserCredentials> traineeRegister(TraineeCreateRequest request, BindingResult bindingResult);

    @Operation(summary = "Get trainee profile", description = "Returns a trainee profile")
    @Parameter(name = "username", description = "The username of the trainee", required = true)
    @ApiResponse(responseCode = "200", description = "Successful retrieval of trainee profile", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetTraineeProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<GetTraineeProfileResponse> getTraineeProfile(String username);

    @Operation(summary = "Update trainee profile", description = "Update trainee profile")
    @Parameter(name = "username", description = "The username of the trainee", required = true)
    @RequestBody(description = "Update trainee profile request", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTraineeProfileRequest.class)))
    @ApiResponse(responseCode = "200", description = "Successful update of trainee profile", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTraineeProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UpdateTraineeProfileResponse> updateTraineeProfile(String username,
                                                                      UpdateTraineeProfileRequest request,
                                                                      BindingResult bindingResult);

    @Operation(summary = "Delete trainee profile", description = "Delete trainee profile")
    @Parameter(name = "username", description = "The username of the trainee", required = true)
    @ApiResponse(responseCode = "200", description = "Successful deletion of trainee profile")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> deleteTraineeProfile(String username);

    @Operation(summary = "Update trainee's trainer list", description = "Update trainee's trainer list")
    @Parameter(name = "username", description = "The username of the trainee", required = true)
    @RequestBody(description = "List of trainers to be updated", required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = TrainerProfileOnlyUsername.class)))
    @ApiResponse(responseCode = "200", description = "Successful update of trainee's trainer list", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = TrainerProfileWithUsername.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<TrainerProfileWithUsername>> updateTraineeTrainerList(String username,
                                                                              List<TrainerProfileOnlyUsername> request);
}
