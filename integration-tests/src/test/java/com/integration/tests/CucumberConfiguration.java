package com.integration.tests;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@ContextConfiguration(classes = {IntegrationTestsApplication.class, TestcontainersConfiguration.class}, initializers = TestcontainersInitializer.class)
public class CucumberConfiguration {
}
