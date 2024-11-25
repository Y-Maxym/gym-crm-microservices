package com.gym.crm.app.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogHandler {

    private final MessageHelper messageHelper;

    public void logBefore(JoinPoint joinPoint, String infoMessageCode, String debugMessageCode) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String transactionId = MDC.get("transactionId");

        Object[] args = joinPoint.getArgs();
        String stringArgs = Arrays.toString(args);

        log.info(messageHelper.getMessage(infoMessageCode, methodName, className, transactionId));
        log.debug(messageHelper.getMessage(debugMessageCode, className, methodName, transactionId, stringArgs));
    }

    public void logAfterReturning(JoinPoint joinPoint, Object result, String infoMessageCode, String debugMessageCode) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String transactionId = MDC.get("transactionId");

        log.info(messageHelper.getMessage(infoMessageCode, methodName, className, transactionId));
        log.debug(messageHelper.getMessage(debugMessageCode, className, methodName, transactionId, result));
    }

    public void logAfterThrowing(JoinPoint joinPoint, Exception ex, String infoMessageCode, String errorMessageCode) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String message = ex.getMessage();
        String transactionId = MDC.get("transactionId");

        log.info(messageHelper.getMessage(infoMessageCode, methodName, className, transactionId));
        log.error(messageHelper.getMessage(errorMessageCode, className, methodName, transactionId, message));
    }
}
