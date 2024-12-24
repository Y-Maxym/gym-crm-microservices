package com.gym.crm.microservices.trainer.hours.service.service.common;

import com.gym.crm.microservices.trainer.hours.service.rest.model.TrainerSummaryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final JmsTemplate template;

    public void sendMessage(String destination, TrainerSummaryRequest request) {
        template.convertAndSend(destination, request);
    }
}
