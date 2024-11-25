package com.gym.crm.app.logging.aspect;

import com.gym.crm.app.logging.LogHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static com.gym.crm.app.util.Constants.DEBUG_FACADE_EXCEPTION;
import static com.gym.crm.app.util.Constants.DEBUG_FACADE_INPUT;
import static com.gym.crm.app.util.Constants.DEBUG_FACADE_RESULT;
import static com.gym.crm.app.util.Constants.INFO_FACADE_EXCEPTION;
import static com.gym.crm.app.util.Constants.INFO_FACADE_INPUT;
import static com.gym.crm.app.util.Constants.INFO_FACADE_RESULT;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FacadeLoggingAspect {

    private final LogHandler logHandler;

    @Pointcut("execution(* com.gym.crm.app.facade..*(..))")
    public void facadeMethods() {
    }

    @Before("facadeMethods()")
    public void logBefore(JoinPoint joinPoint) {
        logHandler.logBefore(joinPoint, INFO_FACADE_INPUT, DEBUG_FACADE_INPUT);
    }

    @AfterReturning(pointcut = "facadeMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logHandler.logAfterReturning(joinPoint, result, INFO_FACADE_RESULT, DEBUG_FACADE_RESULT);
    }

    @AfterThrowing(pointcut = "facadeMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        logHandler.logAfterThrowing(joinPoint, ex, INFO_FACADE_EXCEPTION, DEBUG_FACADE_EXCEPTION);
    }
}
