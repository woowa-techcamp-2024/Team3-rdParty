package com.thirdparty.ticketing.global.lock.lettuce;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.global.lock.CustomSpringELParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LettuceLockAspect {

    @Autowired private LettuceRepository lettuceRepository;

    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    private static final String LETTUCE_LOCK_PREFIX = "seat-lock-";

    @Around("@annotation(com.thirdparty.ticketing.global.lock.lettuce.LettuceLockAnnotation)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LettuceLockAnnotation lettuceLockAnnotation =
                method.getAnnotation(LettuceLockAnnotation.class);
        String lockKey =
                LETTUCE_LOCK_PREFIX
                        + CustomSpringELParser.getDynamicValue(
                                signature.getParameterNames(),
                                joinPoint.getArgs(),
                                lettuceLockAnnotation.key());

        int retryLimit = lettuceLockAnnotation.retryLimit();
        int sleepDuration = lettuceLockAnnotation.sleepDuration();

        try {
            while (retryLimit > 0 && !lettuceRepository.seatLock(lockKey)) {
                retryLimit -= 1;
                Thread.sleep(sleepDuration);
            }

            if (retryLimit > 0) {
                return joinPoint.proceed();
            } else {
                throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
            }

        } catch (InterruptedException e) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT, e);
        } finally {
            lettuceRepository.unlock(lockKey);
        }
    }

    private String parseSpel(String spel, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object rootObject = joinPoint.getTarget(); // 메서드가 호출된 대상 객체
        MethodBasedEvaluationContext context =
                new MethodBasedEvaluationContext(
                        rootObject,
                        methodSignature.getMethod(), // Method
                        joinPoint.getArgs(), // Method Arguments
                        new DefaultParameterNameDiscoverer() // Parameter Name Discoverer
                        );
        return spelExpressionParser.parseExpression(spel).getValue(context, String.class);
    }
}
