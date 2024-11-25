package com.gym.crm.app.it.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.it.AbstractItTest;
import com.gym.crm.app.rest.model.ActivateDeactivateProfileRequest;
import com.gym.crm.app.rest.model.ChangePasswordRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.utils.EntityTestData;
import jakarta.servlet.http.Cookie;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static com.gym.crm.app.rest.exception.ErrorCode.INVALID_USERNAME;
import static com.gym.crm.app.rest.exception.ErrorCode.INVALID_USERNAME_OR_PASSWORD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
public class ItAuthControllerV1Test extends AbstractItTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test login by valid user functionality")
    void givenValidUserCredentials_whenLogin_thenSuccessResponse() throws Exception {
        // given
        UserCredentials userCredentials = EntityTestData.getValidJohnDoeAuthCredentials();

        // when
        ResultActions result = mvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredentials)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test login by invalid user functionality")
    void givenInvalidUserCredentials_whenLogin_thenSuccessResponse() throws Exception {
        // given
        UserCredentials userCredentials = EntityTestData.getInvalidJohnDoeAuthCredentials();

        // when
        ResultActions result = mvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredentials)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(INVALID_USERNAME_OR_PASSWORD.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Invalid username or password")));
    }

    @Test
    @DisplayName("Test logout functionality")
    void givenAuthenticatedUser_whenLogout_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        ResultActions login = login(credentials);
        String token = getAccessToken(login);

        // when
        ResultActions result = mvc.perform(get("/api/v1/logout")
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test logout functionality with cookies")
    void givenAuthenticatedUser_whenLogoutWithCookies_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        ResultActions login = login(credentials);
        String accessToken = getAccessToken(login);
        String refreshToken = getRefreshToken(login);

        // when
        ResultActions result = mvc.perform(get("/api/v1/logout")
                .header("Authorization", accessToken)
                .cookie(new Cookie("refreshToken", refreshToken)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test change password by valid request functionality")
    void givenValidRequest_whenChangePassword_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        ResultActions login = login(credentials);
        String token = getAccessToken(login);

        ChangePasswordRequest request = EntityTestData.getValidChangePasswordRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test change password by invalid request functionality")
    void givenInvalidRequest_whenChangePassword_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        ResultActions login = login(credentials);
        String token = getAccessToken(login);

        ChangePasswordRequest request = EntityTestData.getInvalidChangePasswordRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(INVALID_USERNAME_OR_PASSWORD.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Invalid username or password")));
    }

    @Test
    @DisplayName("Test activate profile functionality")
    void givenValidRequest_whenActivateProfile_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        ResultActions login = login(credentials);
        String token = getAccessToken(login);

        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/{username}/activate")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(patch(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test activate profile by incorrect user functionality")
    void givenInvalidUser_whenActivateProfile_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        ResultActions login = login(credentials);
        String token = getAccessToken(login);

        String username = "Emily.Davis";
        ActivateDeactivateProfileRequest request = EntityTestData.getActivateProfileRequest();

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/{username}/activate")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(patch(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(INVALID_USERNAME.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Invalid username")));
    }

    @Test
    @DisplayName("Test deactivate profile functionality")
    void givenValidRequest_whenDeactivateProfile_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        ResultActions login = login(credentials);
        String token = getAccessToken(login);

        String username = "John.Doe";
        ActivateDeactivateProfileRequest request = EntityTestData.getDeactivateProfileRequest();

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/{username}/activate")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(patch(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test refresh access token with valid refresh token")
    void givenValidRefreshToken_whenRefresh_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        ResultActions login = login(credentials);
        String accessToken = getAccessToken(login);
        String refreshToken = getRefreshToken(login);

        // when
        ResultActions result = mvc.perform(post("/api/v1/refresh")
                .header("Authorization", accessToken)
                .cookie(new Cookie("refreshToken", refreshToken)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", CoreMatchers.startsWith("Bearer ")))
                .andExpect(cookie().value("refreshToken", CoreMatchers.not(refreshToken)));
    }

    private String getAccessToken(ResultActions result) {
        return result.andReturn().getResponse().getHeader("Authorization");
    }

    private String getRefreshToken(ResultActions result) {
        return Optional.ofNullable(result.andReturn().getResponse().getCookie("refreshToken"))
                .map(Cookie::getValue)
                .orElse(null);
    }

    private ResultActions login(UserCredentials userCredentials) throws Exception {
        return mvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredentials)));
    }
}
