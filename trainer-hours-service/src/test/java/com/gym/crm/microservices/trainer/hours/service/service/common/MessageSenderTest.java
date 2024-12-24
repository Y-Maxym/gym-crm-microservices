package com.gym.crm.microservices.trainer.hours.service.service.common;

import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageSenderTest {

    private static final String TRAINER_SUMMARY_QUEUE = "trainer.summary";

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private MessageSender messageSender;

    @Test
    @DisplayName("Test send message functionality")
    void givenRequest_whenSendMessage_thenConvertAndSendCalled() {
        // given
        TrainerSummaryRequest request = EntityTestData.getValidTrainerSummaryRequest();

        // when
        messageSender.sendMessage(TRAINER_SUMMARY_QUEUE, request);

        // then
        verify(jmsTemplate).convertAndSend(TRAINER_SUMMARY_QUEUE, request);
    }
}