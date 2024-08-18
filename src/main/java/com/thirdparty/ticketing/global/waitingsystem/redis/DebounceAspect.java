package com.thirdparty.ticketing.global.waitingsystem.redis;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class DebounceAspect {

    private static final String DEBOUNCE_KEY = "debounce_performance:";

    private final ValueOperations<String, String> debounce;

    public DebounceAspect(StringRedisTemplate redisTemplate) {
        debounce = redisTemplate.opsForValue();
    }

    @Pointcut("@annotation(com.thirdparty.ticketing.global.waitingsystem.Debounce)")
    private void debounceAnnotation() {}

    @Pointcut(
            "execution(public void com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem.moveUserToRunning(long))")
    private void moveWaitingMemberToRunning() {}

    @Around("debounceAnnotation() || moveWaitingMemberToRunning()")
    public Object debounce(ProceedingJoinPoint joinPoint) throws Throwable {
        long performanceId = 0;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Long) {
                performanceId = (long) arg;
            }
        }
        if (debounce.setIfAbsent(getDebounceKey(performanceId), "debounce", 10, TimeUnit.SECONDS)) {
            log.info("[waiting] 디바운스 요청 실행. 공연 ID={}", performanceId);
            return joinPoint.proceed();
        }
        return null;
    }

    private String getDebounceKey(long performanceId) {
        return DEBOUNCE_KEY + performanceId;
    }
}
