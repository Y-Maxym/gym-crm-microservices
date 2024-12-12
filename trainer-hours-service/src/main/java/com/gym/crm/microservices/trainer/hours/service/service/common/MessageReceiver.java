package com.gym.crm.microservices.trainer.hours.service.service.common;

import com.gym.crm.microservices.trainer.hours.service.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.service.TrainerSummaryService;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReceiver {

    private final TrainerSummaryService service;

    @JmsListener(destination = "trainer.summary", containerFactory = "jmsListenerContainerFactory")
    public void sumTrainerSummary(@Payload TrainerSummaryRequest request) {
        service.sumTrainerSummary(request);
    }

    @JmsListener(destination = "ActiveMQ.DLQ", containerFactory = "jmsListenerContainerFactory")
    public void handleDeadLetterQueue(Message message) {
        log.info("Received dead letter queue message: {}", message);
    }
}
