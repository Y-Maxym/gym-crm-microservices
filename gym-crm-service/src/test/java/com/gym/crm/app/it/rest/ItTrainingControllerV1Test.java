package com.gym.crm.app.it.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.client.TrainerHoursClient;
import com.gym.crm.app.it.AbstractItTest;
import com.gym.crm.app.rest.model.AddTrainingRequest;
import com.gym.crm.app.rest.model.UserCredentials;
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

import static com.gym.crm.app.rest.exception.ErrorCode.TRAINING_CREATE_ERROR;
import static com.gym.crm.app.rest.exception.ErrorCode.UNAUTHORIZED_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
public class ItTrainingControllerV1Test extends AbstractItTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TrainerHoursClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test get trainee trainings functionality")
    void givenParams_whenGetTraineesTraining_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        String username = "John.Doe";
        String trainerName = "Emily";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainings/trainees/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .param("profileName", trainerName)
                .header("Authorization", token));
        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].trainerName", CoreMatchers.hasItems(trainerName)));
    }

    @Test
    @DisplayName("Test get trainee trainings by unauthorized user functionality")
    void givenUnauthorizedUser_whenGetTraineesTraining_thenSuccessfulResponse() throws Exception {
        // given
        String username = "John.Doe";
        String trainerName = "Emily";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainings/trainees/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .param("profileName", trainerName));
        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(UNAUTHORIZED_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Unauthorized")));
    }

    @Test
    @DisplayName("Test get trainer trainings functionality")
    void givenParams_whenGetTrainersTraining_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        String username = "Emily.Davis";
        String traineeName = "John";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainings/trainers/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .param("profileName", traineeName)
                .header("Authorization", token));
        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].traineeName", CoreMatchers.hasItems(traineeName)));
    }

    @Test
    @DisplayName("Test get trainee trainings by unauthorized user functionality")
    void givenUnauthorizedUser_whenGetTrainersTraining_thenSuccessfulResponse() throws Exception {
        // given
        String username = "Emily.Davis";
        String traineeName = "John";

        // when
        String uri = UriComponentsBuilder.fromPath("/api/v1/trainings/trainers/{username}")
                .buildAndExpand(username)
                .toUriString();

        ResultActions result = mvc.perform(get(uri)
                .param("profileName", traineeName));
        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(UNAUTHORIZED_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Unauthorized")));
    }

    @Test
    @DisplayName("Test create training functionality")
    void givenValidRequest_whenCreateTraining_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        AddTrainingRequest request = EntityTestData.getValidTrainingRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));
        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test create training by invalid request functionality")
    void givenInvalidRequest_whenCreateTraining_thenSuccessfulResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        AddTrainingRequest request = EntityTestData.getInvalidTrainingRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", token));
        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(TRAINING_CREATE_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Training creation error")));
    }

    @Test
    @DisplayName("Test create training by unauthorized user functionality")
    void givenUnauthorizedUser_whenCreateTraining_thenSuccessfulResponse() throws Exception {
        // given
        AddTrainingRequest request = EntityTestData.getInvalidTrainingRequest();

        // when
        ResultActions result = mvc.perform(post("/api/v1/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
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
