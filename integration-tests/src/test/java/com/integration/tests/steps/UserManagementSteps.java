package com.integration.tests.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.tests.TestcontainersConfiguration;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserManagementSteps extends TestcontainersConfiguration {

    @Autowired
    private WebClient gatewayService;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;
    private String requestBody;
    private String responseBody;
    private String jwtToken;

    @Given("I have an existing user with a username {string} and password {string}")
    public void i_have_an_existing_user_with_a_username_and_password(String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        url = "/authentication-service/api/v1/login";
        requestBody = objectMapper.writeValueAsString(credentials);
    }

    @When("I attempt to authenticate with these credentials")
    public void i_attempt_to_authenticate_with_these_credentials() {
        Mono<String> exchange = gatewayService.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(response -> Mono.justOrEmpty(response.headers().asHttpHeaders().getFirst("Authorization")));

        jwtToken = exchange.block();
    }

    @Then("I receive a successful JWT token")
    public void i_receive_a_successful_JWT_token() {
        assertNotNull(jwtToken, "JWT Token is null");
    }

    @Given("I have data for creating a new user")
    public void i_have_data_for_creating_a_new_user(DataTable dataTable) throws Exception {
        List<Map<String, String>> userData = dataTable.asMaps(String.class, String.class);

        url = "/gym-crm-service/api/v1/trainees/register";
        requestBody = objectMapper.writeValueAsString(userData.get(0));
    }

    @When("I send this data to the server")
    public void i_send_this_data_to_the_server() {
        Mono<String> exchange = gatewayService.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(response -> response.bodyToMono(String.class));

        responseBody = exchange.block();
    }

    @Then("The user is successfully created")
    public void the_user_is_successfully_created() throws Exception {
        Map<String, String> credentials = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertNotNull(responseBody, "Response body is null");
        assertTrue(credentials.containsKey("username"), "Username is null");
        assertTrue(credentials.containsKey("password"), "Password is null");
    }

    @When("I attempt to authenticate with the new user's credentials")
    public void i_attempt_to_authenticate_with_the_new_user_and_password() {
        requestBody = responseBody;
        url = "/authentication-service/api/v1/login";

        Mono<String> exchange = gatewayService.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(response -> Mono.justOrEmpty(response.headers().asHttpHeaders().getFirst("Authorization")));

        jwtToken = exchange.block();
    }

    @Then("The authentication is successful and I receive a JWT token")
    public void the_authentication_is_successful_and_I_receive_a_jwt_token() {
        assertNotNull(jwtToken, "JWT Token is null");
    }
}
