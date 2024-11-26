package com.gym.crm.microservices.trainer.hours.service.rest.impl;

import com.gym.crm.microservices.trainer.hours.service.dto.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.dto.TrainerWorkloadResponse;
import com.gym.crm.microservices.trainer.hours.service.rest.TrainerSummaryController;
import com.gym.crm.microservices.trainer.hours.service.service.TrainerSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/trainer-summary")
@RequiredArgsConstructor
public class TrainerSummaryControllerV1 implements TrainerSummaryController {

    private final TrainerSummaryService service;

    @Override
    @PostMapping
    public ResponseEntity<?> sumWorkload(@RequestBody TrainerSummaryRequest request) {
        service.sumTrainerSummary(request);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getTrainerWorkload(@RequestParam(name = "username") String username,
                                                @RequestParam(name = "year") Integer year,
                                                @RequestParam(name = "month") Integer month) {
        TrainerWorkloadResponse response = service.getTrainerWorkload(username, year, month);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}