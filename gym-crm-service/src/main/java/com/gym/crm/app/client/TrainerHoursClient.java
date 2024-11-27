package com.gym.crm.app.client;

import com.gym.crm.app.rest.model.TrainerSummaryRequest;
import com.gym.crm.app.rest.model.TrainerWorkloadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trainer-hours-service", path = "/api/v1")
public interface TrainerHoursClient {

    @GetMapping("/trainer-summary")
    ResponseEntity<TrainerWorkloadResponse> getTrainerWorkload(
            @RequestParam("username") String username,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month);

    @PostMapping("/trainer-summary")
    ResponseEntity<Void> postTrainerSummary(@RequestBody TrainerSummaryRequest request);
}
