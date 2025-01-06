package com.gym.crm.app.it;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;

@TestConfiguration
public class TestJmsConfiguration {

    @Bean
    @Primary
    public JmsTemplate jmsTemplate() {
        return Mockito.mock(JmsTemplate.class);
    }
}
