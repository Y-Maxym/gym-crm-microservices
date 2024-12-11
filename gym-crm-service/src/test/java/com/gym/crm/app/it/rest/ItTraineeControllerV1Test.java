package com.gym.crm.app.it.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.it.AbstractItTest;
import com.gym.crm.app.rest.model.TraineeCreateRequest;
import com.gym.crm.app.rest.model.TrainerProfileOnlyUsername;
import com.gym.crm.app.rest.model.UpdateTraineeProfileRequest;
import com.gym.crm.app.rest.model.UserCredentials;
import com.gym.crm.app.service.common.MessageSender;
import com.gym.crm.app.utils.EntityTestData;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import java.util.List;

import static com.gym.crm.app.rest.exception.ErrorCode.INVALID_USERNAME;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINEE_CREATE_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINEE_UPDATE_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINEE_WITH_USERNAME_NOT_FOUND;
import static com.gym.crm.app.rest.exception.ErrorCode.TRAINER_WITH_USERNAME_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
public class ItTraineeControllerV1Test extends AbstractItTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageSender messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test register trainee functionality")
    void givenValidRequest_whenRegisterTrainee_thenSuccessfulResponse() throws Exception {
        // given
        TraineeCreateRequest request = EntityTestData.getValidCreateTraineeProfileRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/trainees/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", CoreMatchers.notNullValue()));
    }

    @Test
    @DisplayName("Test register trainee by invalid request functionality")
    void givenInvalidRequest_whenRegisterTrainee_thenSuccessfulResponse() throws Exception {
        // given
        TraineeCreateRequest request = EntityTestData.getInvalidCreateTraineeProfileRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/trainees/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINEE_CREATE_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Trainee creation error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[*].field", CoreMatchers.hasItems("firstName", "lastName")));
    }

    @Test
    @DisplayName("Test get trainee profile functionality")
    void givenAuthenticateUser_whenGetTrainee_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Doe")));
    }

    @Test
    @DisplayName("Test get trainee profile by invalid username functionality")
    void givenAuthenticateUserAndInvalidUsername_whenGetTrainee_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        String username = "invalid";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINEE_WITH_USERNAME_NOT_FOUND.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Trainee with username %s not found".formatted(username))));
    }

    @Test
    @DisplayName("Test update trainee by valid request functionality")
    void givenValidRequest_whenUpdateTrainee_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        UpdateTraineeProfileRequest request = EntityTestData.getValidTraineeProfileRequest();
        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}")
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
    @DisplayName("Test update trainee by invalid request functionality")
    void givenInvalidRequest_whenUpdateTrainee_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        UpdateTraineeProfileRequest request = EntityTestData.getInvalidTraineeProfileRequest();
        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINEE_UPDATE_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Trainee update error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields[*].field", CoreMatchers.hasItems("firstName", "lastName")));
    }

    @Test
    @DisplayName("Test update trainee by invalid username functionality")
    void givenInvalidUsername_whenUpdateTrainee_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        UpdateTraineeProfileRequest request = EntityTestData.getValidTraineeProfileRequest();
        String username = "Emily.Davis";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}")
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
    @DisplayName("Test delete trainee profile functionality")
    void givenAuthenticateUser_whenDeleteTrainee_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(delete(uri)
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test delete trainee profile by invalid username functionality")
    void givenAuthenticateUserAndInvalidUsername_whenDeleteTrainee_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        String username = "Emily.Davis";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(delete(uri)
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(INVALID_USERNAME.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Invalid username")));
    }

    @Test
    @DisplayName("Test update trainee trainers list functionality")
    void givenValidRequest_whenUpdateTraineeTrainerList_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        String username = "John.Doe";
        List<TrainerProfileOnlyUsername> request = EntityTestData.getValidListTrainerProfileOnlyUsernames();

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}/trainers")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].username", CoreMatchers.hasItems(request.get(0).getUsername(), request.get(1).getUsername())));
    }

    @Test
    @DisplayName("Test update trainee trainers list by invalid username functionality")
    void givenInvalidUsername_whenUpdateTraineeTrainerList_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        List<TrainerProfileOnlyUsername> request = EntityTestData.getValidListTrainerProfileOnlyUsernames();
        String username = "Emily.Davis";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}/trainers")
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
    @DisplayName("Test update trainee trainers list by invalid request functionality")
    void givenInvalidRequest_whenUpdateTraineeTrainerList_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        List<TrainerProfileOnlyUsername> request = EntityTestData.getInvalidListTrainerProfileOnlyUsernames();
        String username = "John.Doe";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainees/{username}/trainers")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINER_WITH_USERNAME_NOT_FOUND.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Trainer with username %s not found".formatted(request.get(0).getUsername()))));
    }

    private String login(UserCredentials userCredentials) throws Exception {
        ResultActions result = mvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredentials)));

        return result.andReturn().getResponse().getHeader("Authorization");
    }
}
