package com.gym.crm.app.rest;

import com.gym.crm.app.rest.model.ErrorResponse;
import com.gym.crm.app.rest.model.GetTrainerProfileResponse;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.rest.model.TrainerProfileWithUsername;
import com.gym.crm.app.rest.model.UpdateTrainerProfileRequest;
import com.gym.crm.app.rest.model.UpdateTrainerProfileResponse;
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

@Tag(name = "Trainers", description = "Trainer endpoints")
public interface TrainerController {

    @RequestBody(description = "Trainer registration request", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerCreateRequest.class)))
    @Operation(summary = "Register a new trainer", description = "Register a new trainer in the gym.")
    @ApiResponse(responseCode = "200", description = "Trainer successfully registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCredentials.class)))
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserCredentials> trainerRegister(TrainerCreateRequest request,
                                                    BindingResult bindingResult);

    @Parameter(name = "username", description = "The username of the trainer", required = true)
    @Operation(summary = "Get trainer profile", description = "Retrieve the profile of a trainer by username.")
    @ApiResponse(responseCode = "200", description = "Trainer profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetTrainerProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<GetTrainerProfileResponse> getTrainerProfile(String username);

    @Parameter(description = "Username of the trainer", required = true)
    @RequestBody(description = "Trainer profile update request", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTrainerProfileRequest.class)))
    @Operation(summary = "Update trainer profile", description = "Update the profile of a trainer by username.")
    @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTrainerProfileResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UpdateTrainerProfileResponse> updateTrainerProfile(String username,
                                                                      UpdateTrainerProfileRequest request,
                                                                      BindingResult bindingResult);

    @Parameter(description = "Username of the trainee", required = true)
    @Operation(summary = "Get active trainers not assigned to trainee", description = "Retrieve a list of active trainers who are not assigned to a specific trainee by username.")
    @ApiResponse(responseCode = "200", description = "List of active trainers retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerProfileWithUsername.class)))
    @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<TrainerProfileWithUsername>> getTrainersNotAssigned(String username);
}
