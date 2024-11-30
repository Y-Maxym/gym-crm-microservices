package com.gym.crm.app.client;

import com.gym.crm.app.service.common.dto.TrainerSummaryRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trainer-hours-service", path = "/api/v1")
public interface TrainerHoursClient {

    @GetMapping("/trainer-summary")
    @CircuitBreaker(name = "trainerHoursClientCircuitBreaker", fallbackMethod = "fallbackMethod")
    ResponseEntity<?> getTrainerWorkload(
            @RequestParam("username") String username,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month);

    @PostMapping("/trainer-summary")
    @CircuitBreaker(name = "trainerHoursClientCircuitBreaker", fallbackMethod = "fallbackMethod")
    ResponseEntity<?> postTrainerSummary(@RequestBody TrainerSummaryRequest request);

    default ResponseEntity<?> fallbackMethod(Throwable throwable) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
