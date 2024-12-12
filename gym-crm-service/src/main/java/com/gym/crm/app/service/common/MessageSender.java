package com.gym.crm.app.service.common;

import com.gym.crm.app.service.common.dto.TrainerSummaryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private static final String TRAINER_SUMMARY_QUEUE = "trainer.summary";

    private final JmsTemplate template;

    public void sendMessage(TrainerSummaryRequest request) {
        template.convertAndSend(TRAINER_SUMMARY_QUEUE, request);
    }
}
