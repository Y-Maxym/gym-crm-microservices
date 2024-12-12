package com.gym.crm.microservices.trainer.hours.service.service.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.service.TrainerSummaryService;
import com.gym.crm.microservices.trainer.hours.service.utils.EntityTestData;
import jakarta.jms.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageReceiverTest {

    @Mock
    private TrainerSummaryService service;

    @InjectMocks
    private MessageReceiver receiver;

    @Test
    @DisplayName("Test receive message for sum trainer summary functionality")
    void givenRequest_whenSumTrainerSummary_thenServiceIsCalled() {
        // given
        TrainerSummaryRequest request = EntityTestData.getValidTrainerSummaryRequest();

        // when
        receiver.sumTrainerSummary(request);

        // then
        verify(service).sumTrainerSummary(request);
    }

    @Test
    @DisplayName("Test receive message from dead letter queue functionality")
    void givenRequest_whenDeadLetterQueue_thenMessageIsReceived() {
        // given
        Message message = mock(Message.class);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(MessageReceiver.class);
        logger.addAppender(listAppender);

        // when
        receiver.handleDeadLetterQueue(message);

        // then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    }
}