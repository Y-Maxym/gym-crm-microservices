package com.gym.crm.microservices.trainer.hours.service.it;

import com.gym.crm.microservices.trainer.hours.service.TrainerHoursService;
import com.gym.crm.microservices.trainer.hours.service.config.MongoDbTestContainerConfiguration;
import com.gym.crm.microservices.trainer.hours.service.config.TestJmsConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(classes = {TrainerHoursService.class, TestJmsConfiguration.class})
@CucumberContextConfiguration
public class CucumberConfiguration extends MongoDbTestContainerConfiguration {
}
