package com.thirdparty.ticketing.global.waitingsystem.memory;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.domain.waitingsystem.Debounce;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(MemoryDebounceAspectTest.MemoryDebounceAopConfig.class)
class MemoryDebounceAspectTest {

    @Autowired private MemoryDebounceAspectTest.MemoryDebounceTarget memoryDebounceTarget;

    private static final ConcurrentHashMap<Long, MemoryDebounceAspect.DebounceInfo> debounceMap =
            new ConcurrentHashMap<>();

    @Configuration
    @EnableAspectJAutoProxy
    static class MemoryDebounceAopConfig {

        @Bean
        public MemoryDebounceAspect memoryDebounceAspect() {
            return new MemoryDebounceAspect(debounceMap);
        }

        @Bean
        public MemoryDebounceTarget debounceTarget() {
            return new MemoryDebounceTarget();
        }
    }

    public static class MemoryDebounceTarget {

        private final AtomicInteger counter;

        public MemoryDebounceTarget() {
            counter = new AtomicInteger(0);
        }

        @Debounce
        public void increment(long performanceId) {
            counter.incrementAndGet();
        }

        public int get() {
            return counter.get();
        }

        public void reset() {
            counter.set(0);
            debounceMap.clear();
        }
    }

    @BeforeEach
    void setUp() {
        memoryDebounceTarget.reset();
    }

    @Nested
    @DisplayName("메모리 디바운스 aop 적용 시")
    class MemoryDebounceTest {

        @Test
        @DisplayName("동시에 한 번만 실행된다.")
        void debounce() throws InterruptedException {
            // given
            int poolSize = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
            CountDownLatch latch = new CountDownLatch(poolSize);

            // when
            for (int i = 0; i < poolSize; i++) {
                executorService.execute(
                        () -> {
                            try {
                                memoryDebounceTarget.increment(1);
                            } finally {
                                latch.countDown();
                            }
                        });
            }
            latch.await();

            // then
            assertThat(memoryDebounceTarget.get()).isEqualTo(1);
        }

        @Test
        @Disabled("15초짜리 테스트는 너무 길다.")
        @DisplayName("15초 동안 1초마다 호출하면 2번만 실행된다.")
        void debounceWithTimeInterval() throws InterruptedException {
            // given
            int totalCalls = 15;
            long performanceId = 1L;
            CountDownLatch latch = new CountDownLatch(totalCalls);

            // when
            for (int i = 0; i < totalCalls; i++) {
                memoryDebounceTarget.increment(performanceId);
                latch.countDown();
                if (i < totalCalls - 1) { // 마지막 호출 후에는 대기하지 않음
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            latch.await();

            // then
            assertThat(memoryDebounceTarget.get()).isEqualTo(2);
        }
    }
}
