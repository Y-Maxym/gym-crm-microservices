package com.gym.crm.app.it.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.it.AbstractItTest;
import com.gym.crm.app.rest.model.TrainerCreateRequest;
import com.gym.crm.app.rest.model.UpdateTrainerProfileRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.utils.EntityTestData;
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

import static com.gym.crm.app.rest.exception.ErrorCode.INVALID_USERNAME;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINER_CREATE_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINER_UPDATE_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINER_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.rest.exception.ErrorCode.UNAUTHORIZED_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
public class ItTrainerControllerV1Test extends AbstractItTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test register trainer functionality")
    void givenValidRequest_whenRegisterTrainer_thenSuccessfulResponse() throws Exception {
        // given
        TrainerCreateRequest request = EntityTestData.getValidCreateTrainerProfileRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test register trainer by invalid request functionality")
    void givenInvalidRequest_whenRegisterTrainer_thenSuccessfulResponse() throws Exception {
        // given
        TrainerCreateRequest request = EntityTestData.getInvalidCreateTrainerProfileRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINER_CREATE_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Trainer creation error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[*].field", CoreMatchers.hasItems("firstName", "lastName")));
    }

    @Test
    @DisplayName("Test get trainer profile functionality")
    void givenAuthenticateUser_whenGetTrainer_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        String token = login(credentials);

        String username = "Emily.Davis";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainers/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("Emily")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Davis")));
    }

    @Test
    @DisplayName("Test get trainer profile by invalid username functionality")
    void givenAuthenticateUserAndInvalidUsername_whenGetTrainer_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        String token = login(credentials);

        String username = "invalid";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainers/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINER_WITH_USERNAME_NOT_FOUND.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Trainer with username %s not found".formatted(username))));
    }

    @Test
    @DisplayName("Test update trainer by valid request functionality")
    void givenValidRequest_whenUpdateTrainer_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        String token = login(credentials);

        UpdateTrainerProfileRequest request = EntityTestData.getValidUpdateTrainerProfileRequest();
        String username = "Emily.Davis";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainers/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test update trainer by invalid request functionality")
    void givenInvalidRequest_whenUpdateTrainer_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        String token = login(credentials);

        UpdateTrainerProfileRequest request = EntityTestData.getInvalidTrainerProfileRequest();
        String username = "Emily.Davis";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainers/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINER_UPDATE_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Trainer update error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[*].field", CoreMatchers.hasItems("firstName", "lastName")));
    }

    @Test
    @DisplayName("Test update trainer by invalid username functionality")
    void givenInvalidUsername_whenUpdateTrainer_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        String token = login(credentials);

        UpdateTrainerProfileRequest request = EntityTestData.getValidUpdateTrainerProfileRequest();
        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainers/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(INVALID_USERNAME.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Invalid username")));
    }

    @Test
    @DisplayName("Test get trainers not assigned by valid trainee username functionality")
    void givenValidUsername_whenGetTrainersNotAssigned_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidEmilyDavisAuthCredentials();
        String token = login(credentials);

        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainers/not-assigned/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].username", CoreMatchers.hasItems("David.Brown")));
    }


    @Test
    @DisplayName("Test get trainers not assigned by unauthorized user functionality")
    void givenUnauthorizedUser_whenGetTrainersNotAssigned_thenSuccessfulResponse() throws Exception {
        // given
        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainers/not-assigned/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(put(uri));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(UNAUTHORIZED_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Unauthorized")));
    }

    private String login(UserCredentials userCredentials) throws Exception {
        ResultActions result = mvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredentials)));

        return result.andReturn().getResponse().getHeader("Authorization");
    }
}
