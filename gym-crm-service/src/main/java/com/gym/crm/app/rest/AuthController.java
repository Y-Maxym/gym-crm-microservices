package com.gym.crm.app.rest;

import com.gym.crm.app.rest.model.ActivateDeactivateProfileRequest;
import com.gym.crm.app.rest.model.ChangePasswordRequest;
import com.gym.crm.app.rest.model.ErrorResponse;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.rest.model.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@Tag(name = "Auth", description = "Auth endpoints")
public interface AuthController {

    @RequestBody(description = "User credentials for login", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCredentials.class)))
    @Operation(summary = "Login", description = "Login to the system")
    @ApiResponse(responseCode = "200", description = "Successful login")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> login(UserCredentials credentials,
                            BindingResult bindingResult,
                            HttpServletRequest request,
                            HttpServletResponse response);

    @Operation(summary = "Logout", description = "Logout from the system")
    @ApiResponse(responseCode = "200", description = "Successful logout")
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> logout(HttpServletRequest request);

    @Operation(summary = "Change login password", description = "Change the password for login")
    @RequestBody(description = "Request to change password", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangePasswordRequest.class)))
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> changePassword(ChangePasswordRequest request,
                                     BindingResult bindingResult);

    @Parameter(name = "username", description = "Username of the user", required = true)
    @RequestBody(description = "Request to activate/deactivate user profile", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivateDeactivateProfileRequest.class)))
    @Operation(summary = "Activate/Deactivate user profile", description = "Activate or deactivate a trainee profile")
    @ApiResponse(responseCode = "200", description = "User profile activated/deactivated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> activateDeactivateProfile(String username,
                                                ActivateDeactivateProfileRequest request,
                                                BindingResult bindingResult);

    @Parameter(name = "token", description = "Refresh token", required = true)
    @Operation(summary = "Refresh access token", description = "Refresh access token")
    @ApiResponse(responseCode = "200", description = "Successfully refresh access token")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> refreshAccessToken(String token, HttpServletRequest request);
}
