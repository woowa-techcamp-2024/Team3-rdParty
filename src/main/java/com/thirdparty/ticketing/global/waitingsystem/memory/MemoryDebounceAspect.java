package com.thirdparty.ticketing.global.waitingsystem.memory;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class MemoryDebounceAspect {

    private final ConcurrentHashMap<Long, DebounceInfo> debounceMap = new ConcurrentHashMap<>();
    private static final long DEBOUNCE_MILLIS = 10_000; // 10초

    private static class DebounceInfo {
        volatile Instant lastExecution;
        final ReentrantLock lock;

        DebounceInfo() {
            this.lastExecution = Instant.EPOCH; // 1970-01-01T00:00:00Z
            this.lock = new ReentrantLock();
        }
    }

    @Pointcut("@annotation(com.thirdparty.ticketing.global.waitingsystem.Debounce)")
    private void debounceAnnotation() {}

    @Pointcut(
            "execution(public void com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem.moveUserToRunning(long))")
    private void moveWaitingMemberToRunning() {}

    @Around("debounceAnnotation() || moveWaitingMemberToRunning()")
    public Object debounce(ProceedingJoinPoint joinPoint) throws Throwable {
        long performanceId = extractPerformanceId(joinPoint);
        DebounceInfo info = debounceMap.computeIfAbsent(performanceId, k -> new DebounceInfo());

        if (info.lock.tryLock()) {
            try {
                Instant now = Instant.now();
                if (now.isAfter(info.lastExecution.plusMillis(DEBOUNCE_MILLIS))) {
                    info.lastExecution = now;
                    log.info("[waiting] 디바운스 요청 실행. 공연 ID={}", performanceId);
                    return joinPoint.proceed();
                }
            } finally {
                info.lock.unlock();
            }
        }

        log.info("[waiting] 디바운스로 인한 요청 무시. 공연 ID={}", performanceId);
        return null;
    }

    private long extractPerformanceId(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        throw new IllegalArgumentException("메서드 인자에서 공연 ID를 찾을 수 없습니다.");
    }
}
