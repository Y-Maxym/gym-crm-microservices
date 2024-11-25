package com.gym.crm.app.it.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.it.AbstractItTest;
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
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.gym.crm.app.rest.exception.ErrorCode.UNAUTHORIZED_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
class ItAuthenticationFilterTest extends AbstractItTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test login request without session functionality")
    void givenUserCredentials_whenLogin_thenSuccessResponse() throws Exception {
        // given
        UserCredentials userCredentials = EntityTestData.getValidJohnDoeAuthCredentials();

        // when
        ResultActions result = mvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredentials)));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Authorization"));
    }

    @Test
    @DisplayName("Test unauthorized access to protected URL without authorized user functionality")
    void givenProtectedURLAndUnauthorizedUser_whenPerform_thenSuccessResponse() throws Exception {
        // given

        // when
        ResultActions result = mvc.perform(get("/api/v1/training-types"));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(UNAUTHORIZED_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.containsString("Unauthorized")));
    }

    @Test
    @DisplayName("Test unauthorized access to protected URL without session user functionality")
    void givenProtectedURLAndNoSession_whenPerform_thenSuccessResponse() throws Exception {
        // given

        // when
        ResultActions result = mvc.perform(get("/api/v1/training-types"));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(UNAUTHORIZED_ERROR.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.containsString("Unauthorized")));
    }

    @Test
    @DisplayName("Test authorized access to protected URL with session functionality")
    void givenProtectedUrlAndAuthorizedUser_whenPerform_thenSuccessResponse() throws Exception {
        // given
        UserCredentials credentials = EntityTestData.getValidJohnDoeAuthCredentials();
        String token = login(credentials);

        // when
        ResultActions result = mvc.perform(get("/api/v1/training-types")
                .header("Authorization", token));

        // then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].trainingType", CoreMatchers.hasItems("Yoga", "Fitness", "Zumba", "Stretching")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].trainingTypeId", CoreMatchers.hasItems(1, 2)));
    }

    private String login(UserCredentials userCredentials) throws Exception {
        ResultActions result = mvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredentials)));

        return result.andReturn().getResponse().getHeader("Authorization");
    }

}