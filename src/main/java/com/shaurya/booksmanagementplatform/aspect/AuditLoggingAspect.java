package com.shaurya.booksmanagementplatform.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditLoggingAspect {

    @Before("execution(* com.shaurya.booksmanagementplatform.service.impl..*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling method: {}",
                joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "execution(* com.shaurya.booksmanagementplatform.service.impl..*.*(..))")
    public void logAfterReturning(JoinPoint joinPoint) {
        log.info("Method {} completed successfully", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "execution(* com.shaurya.booksmanagementplatform.service.impl..*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Method {} threw exception: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }
}
