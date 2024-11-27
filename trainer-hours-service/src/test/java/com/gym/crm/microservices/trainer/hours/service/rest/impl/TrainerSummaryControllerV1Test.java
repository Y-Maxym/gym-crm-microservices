package com.gym.crm.microservices.trainer.hours.service.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.trainer.hours.service.rest.TrainerSummaryControllerV1;
import com.gym.crm.microservices.trainer.hours.service.exception.DataNotFoundException;
import com.gym.crm.microservices.trainer.hours.service.rest.exception.GlobalExceptionHandler;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerWorkloadResponse;
import com.gym.crm.microservices.trainer.hours.service.service.TrainerSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerSummaryControllerV1Test.class)
class TrainerSummaryControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Mock
    private TrainerSummaryService service;

    @InjectMocks
    private TrainerSummaryControllerV1 controller;

    private TrainerSummaryRequest request;
    private TrainerWorkloadResponse workloadResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        request = new TrainerSummaryRequest()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(120)
                .actionType(TrainerSummaryRequest.ActionTypeEnum.ADD);

        workloadResponse = new TrainerWorkloadResponse()
                .workload(120);
    }

    @Test
    @DisplayName("Test sumWorkload returns OK status")
    void givenValidRequest_whenSumWorkload_thenReturnOk() throws Exception {
        // given
        BDDMockito.willDoNothing().given(service).sumTrainerSummary(any(TrainerSummaryRequest.class));

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/trainer-summary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        // then
        result.andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).sumTrainerSummary(any(TrainerSummaryRequest.class));
    }

    @Test
    @DisplayName("Test getTrainerWorkload returns OK with correct data")
    void givenValidParams_whenGetTrainerWorkload_thenReturnOkWithWorkload() throws Exception {
        // given
        BDDMockito.given(service.getTrainerWorkload(request.getUsername(), 2024, 5))
                .willReturn(workloadResponse);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/trainer-summary")
                .param("username", request.getUsername())
                .param("year", String.valueOf(request.getTrainingDate().getYear()))
                .param("month", String.valueOf(request.getTrainingDate().getMonthValue())));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workload").value(120));

        verify(service, times(1)).getTrainerWorkload("John.Doe", 2024, 5);
    }

    @Test
    @DisplayName("Test getTrainerWorkload returns 404 when data not found")
    void givenNoWorkload_whenGetTrainerWorkload_thenReturnNotFound() throws Exception {
        // given
        BDDMockito.given(service.getTrainerWorkload("trainer2", 2024, 5))
                .willThrow(new DataNotFoundException("User information not found"));

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/trainer-summary")
                .param("username", "trainer2")
                .param("year", "2024")
                .param("month", "5"));

        // then
        result.andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).getTrainerWorkload("trainer2", 2024, 5);
    }
}