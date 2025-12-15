package com.technokratos;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@within(org.springframework.stereotype.Controller)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodInfo(joinPoint, false);
    }

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodInfo(joinPoint, false);
    }

    @Around("@within(org.springframework.stereotype.Repository)")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodInfo(joinPoint, true);
    }

    private Object logMethodInfo(ProceedingJoinPoint joinPoint, boolean logExecutionTime) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        Object methodResult = joinPoint.proceed();
        long stopTime = System.currentTimeMillis();
        if (logExecutionTime) {
            log.info("Class: {}, method: {}, args: {}, execution time: {} ms",
                    className, methodName, Arrays.toString(methodArgs), stopTime - startTime);
        } else {
            log.info("Class: {}, method: {}, args: {}",
                    className, methodName, Arrays.toString(methodArgs));
        }
        return methodResult;
    }
}