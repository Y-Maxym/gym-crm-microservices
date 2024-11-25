package com.gym.crm.app.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class LogHandlerTest {

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private LogHandler logHandler;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(LogHandler.class);

        listAppender = new ListAppender<>();
        listAppender.setContext(loggerContext);
        listAppender.start();

        logger.addAppender(listAppender);
        logger.setLevel(Level.TRACE);
    }

    @Test
    @DisplayName("Test log before method")
    void givenJoinPoint_whenLogBefore_thenLogsInfoAndDebugMessages() {
        // given
        String methodName = "testMethod";
        String className = "TestClass";
        String transactionId = "transactionId";
        Object[] args = {1, "test"};
        String infoMessage = "Info message";
        String debugMessage = "Debug message";

        MockedStatic<MDC> mockedStatic = mockStatic(MDC.class);
        mockedStatic.when(() -> MDC.get("transactionId")).thenReturn(transactionId);

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getName()).willReturn(methodName);
        given(signature.getDeclaringTypeName()).willReturn(className);
        given(joinPoint.getArgs()).willReturn(args);
        given(messageHelper.getMessage("INFO_CODE", methodName, className, transactionId)).willReturn(infoMessage);
        given(messageHelper.getMessage("DEBUG_CODE", className, methodName, transactionId, Arrays.toString(args))).willReturn(debugMessage);

        // when
        logHandler.logBefore(joinPoint, "INFO_CODE", "DEBUG_CODE");

        // then
        assertThat(listAppender.list).extracting("formattedMessage")
                .containsExactly(infoMessage, debugMessage);

        mockedStatic.close();
    }

    @Test
    @DisplayName("Test log after returning method")
    void givenJoinPointAndResult_whenLogAfterReturning_thenLogsInfoAndDebugMessages() {
        // given
        String methodName = "testMethod";
        String className = "TestClass";
        String transactionId = "transactionId";
        Object result = "result";
        String infoMessage = "Info message";
        String debugMessage = "Debug message";

        MockedStatic<MDC> mockedStatic = mockStatic(MDC.class);
        mockedStatic.when(() -> MDC.get("transactionId")).thenReturn(transactionId);

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getName()).willReturn(methodName);
        given(signature.getDeclaringTypeName()).willReturn(className);
        given(messageHelper.getMessage("INFO_CODE", methodName, className, transactionId)).willReturn(infoMessage);
        given(messageHelper.getMessage("DEBUG_CODE", className, methodName, transactionId, result)).willReturn(debugMessage);

        // when
        logHandler.logAfterReturning(joinPoint, result, "INFO_CODE", "DEBUG_CODE");

        // then
        assertThat(listAppender.list).extracting("formattedMessage")
                .containsExactly(infoMessage, debugMessage);

        mockedStatic.close();
    }

    @Test
    @DisplayName("Test log after throwing method")
    void givenJoinPointAndException_whenLogAfterThrowing_thenLogsInfoAndErrorMessages() {
        // given
        String methodName = "testMethod";
        String className = "TestClass";
        String transactionId = "transactionId";
        Exception ex = new RuntimeException("Error occurred");
        String infoMessage = "Info message";
        String errorMessage = "Error message";

        MockedStatic<MDC> mockedStatic = mockStatic(MDC.class);
        mockedStatic.when(() -> MDC.get("transactionId")).thenReturn(transactionId);

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getName()).willReturn(methodName);
        given(signature.getDeclaringTypeName()).willReturn(className);
        given(messageHelper.getMessage("INFO_CODE", methodName, className, transactionId)).willReturn(infoMessage);
        given(messageHelper.getMessage("ERROR_CODE", className, methodName, transactionId, ex.getMessage())).willReturn(errorMessage);

        // when
        logHandler.logAfterThrowing(joinPoint, ex, "INFO_CODE", "ERROR_CODE");

        // then
        assertThat(listAppender.list).extracting("formattedMessage")
                .containsExactly(infoMessage, errorMessage);

        mockedStatic.close();
    }
}
