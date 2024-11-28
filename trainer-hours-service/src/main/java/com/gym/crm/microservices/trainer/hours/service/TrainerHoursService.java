package com.gym.crm.microservices.trainer.hours.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainerHoursService {

    public static void main(String[] args) {
        SpringApplication.run(TrainerHoursService.class, args);
    }

}
