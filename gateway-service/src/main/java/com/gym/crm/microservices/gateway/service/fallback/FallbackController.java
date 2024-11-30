package com.gym.crm.microservices.gateway.service.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    private static final int GYM_CRM_SERVICE_UNAVAILABLE_CODE = 8081;
    private static final String CYM_CRM_SERVICE_UNAVAILABLE_MESSAGE = "Gym CRM Service is currently unavailable. Please try again later.";
    private static final int TRAINER_HOURS_SERVICE_UNAVAILABLE_CODE = 8082;
    private static final String TRAINER_HOURS_SERVICE_UNAVAILABLE_MESSAGE = "Trainer Hours Service is currently unavailable. Please try again later.";

    @RequestMapping("/fallback/gym-crm-service")
    public ResponseEntity<?> fallbackGymCrmService() {
        ErrorEntity error = new ErrorEntity(GYM_CRM_SERVICE_UNAVAILABLE_CODE, CYM_CRM_SERVICE_UNAVAILABLE_MESSAGE);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }

    @RequestMapping("/fallback/trainer-hours-service")
    public ResponseEntity<?> fallbackTrainerHoursService() {
        ErrorEntity error = new ErrorEntity(TRAINER_HOURS_SERVICE_UNAVAILABLE_CODE, TRAINER_HOURS_SERVICE_UNAVAILABLE_MESSAGE);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }
}
