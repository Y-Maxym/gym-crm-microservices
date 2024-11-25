package com.gym.crm.app.rest;

import com.gym.crm.app.rest.model.ErrorResponse;
import com.gym.crm.app.rest.model.GetTrainingTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Training Types", description = "Training Types endpoints")
public interface TrainingTypeController {

    @Operation(summary = "Get training types", description = "Retrieve the list of available training types")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of training types", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = GetTrainingTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<GetTrainingTypeResponse>> getAllTrainingTypes();
}
