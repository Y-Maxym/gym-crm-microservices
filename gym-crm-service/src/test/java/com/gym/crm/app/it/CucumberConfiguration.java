package com.gym.crm.app.it;

import com.gym.crm.app.CrmGymApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = {CrmGymApplication.class, TestJmsConfiguration.class})
@CucumberContextConfiguration
public class CucumberConfiguration extends AbstractItTest {
}
