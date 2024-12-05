package com.gym.crm.microservices.gateway.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GatewayService {

	public static void main(String[] args) {
		SpringApplication.run(GatewayService.class, args);
	}

}
