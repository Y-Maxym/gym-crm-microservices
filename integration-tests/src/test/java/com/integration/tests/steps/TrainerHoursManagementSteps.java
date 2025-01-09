package com.integration.tests.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainerHoursManagementSteps {

    @Autowired
    private WebClient gatewayService;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;
    private String requestBody;
    private String responseBody;
    private String jwtToken;

    @Given("I have user credentials")
    public void i_have_user_credentials(DataTable dataTable) throws Exception {
        Map<String, String> credentials = dataTable.asMap(String.class, String.class);

        url = "/authentication-service/api/v1/login";
        requestBody = objectMapper.writeValueAsString(credentials);
    }

    @When("I send a request for authentication to authentication-service")
    public void i_send_a_request_for_authentication_to() {
        Mono<String> exchange = gatewayService.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(response -> Mono.justOrEmpty(response.headers().asHttpHeaders().getFirst("Authorization")));

        jwtToken = exchange.block();
    }

    @Then("I should receive a valid JWT token")
    public void i_should_receive_a_valid_jwt_token() {
        assertNotNull(jwtToken, "JWT Token is null");
    }

    @When("I register a new training session for a trainer with gym-crm-service")
    public void i_register_new_training_session_for_a_trainer_with(DataTable dataTable) throws Exception {
        url = "/gym-crm-service/api/v1/trainings";
        Map<String, String> training = dataTable.asMap(String.class, String.class);
        requestBody = objectMapper.writeValueAsString(training);

        Mono<Void> exchange = gatewayService.post()
                .uri(url)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(response -> response.bodyToMono(Void.class));

        exchange.block();
    }

    @When("I attempt to retrieve the training session details from trainer-hours-service")
    public void i_attempt_to_retrieve_the_training_session_details_from_trainer_hours_service() {
        url = "/trainer-hours-service/api/v1/trainer-summary";

        Mono<String> exchange = gatewayService.get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("username", "Emily.Davis")
                        .queryParam("year", 2023)
                        .queryParam("month", 9)
                        .build())
                .header("Authorization", jwtToken)
                .exchangeToMono(response -> response.bodyToMono(String.class));

        responseBody = exchange.block();
    }

    @Then("I can verify the training session details with trainer-hours-service")
    public void i_can_verify_the_training_session_details_with_trainer_hours_service() throws Exception {
        Map<String, String> trainerWorkload = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertTrue(trainerWorkload.containsKey("workload"));
        assertNotNull(trainerWorkload.get("workload"));
    }
}
