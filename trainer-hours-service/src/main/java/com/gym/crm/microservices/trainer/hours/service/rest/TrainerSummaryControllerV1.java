package com.gym.crm.microservices.trainer.hours.service.rest;

import com.gym.crm.microservices.trainer.hours.service.model.TrainerWorkloadResponse;
import com.gym.crm.microservices.trainer.hours.service.service.TrainerSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/trainer-summary")
@RequiredArgsConstructor
public class TrainerSummaryControllerV1 {

    private final TrainerSummaryService service;

    @GetMapping
    public ResponseEntity<?> getTrainerWorkload(@RequestParam(name = "username") String username,
                                                @RequestParam(name = "year") Integer year,
                                                @RequestParam(name = "month") Integer month) {
        TrainerWorkloadResponse response = service.getTrainerWorkload(username, year, month);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
