package com.thirdparty.ticketing.global.lock.redisson;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.global.lock.CustomSpringELParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class RedissonLockAspect {

    @Autowired private RedissonClient redissonClient;

    private static final String REDISSON_LOCK_PREFIX = "seat-lock-";

    @Around("@annotation(com.thirdparty.ticketing.global.lock.redisson.RedissonLockAnnotation)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLockAnnotation redissonLockAnnotation =
                method.getAnnotation(RedissonLockAnnotation.class);
        String lockKey =
                REDISSON_LOCK_PREFIX
                        + CustomSpringELParser.getDynamicValue(
                                signature.getParameterNames(),
                                joinPoint.getArgs(),
                                redissonLockAnnotation.key());

        int waitTime = redissonLockAnnotation.waitTime();
        int lockTTL = redissonLockAnnotation.lockTTL();

        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(waitTime, lockTTL, TimeUnit.SECONDS)) {
                return false;
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT, e);
        } finally {
            lock.unlock();
        }
    }
}
