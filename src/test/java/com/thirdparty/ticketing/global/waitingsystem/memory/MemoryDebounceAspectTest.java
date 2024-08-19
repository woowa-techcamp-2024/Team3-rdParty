package com.thirdparty.ticketing.global.waitingsystem.memory;

import com.thirdparty.ticketing.global.waitingsystem.Debounce;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MemoryDebounceAspectTest.MemoryDebounceAopConfig.class)
class MemoryDebounceAspectTest {

    @Autowired
    private MemoryDebounceAspectTest.MemoryDebounceTarget memoryDebounceTarget;

    @Configuration
    @EnableAspectJAutoProxy
    static class MemoryDebounceAopConfig {

        @Bean
        public MemoryDebounceAspect memoryDebounceAspect() {
            return new MemoryDebounceAspect();
        }

        @Bean
        public MemoryDebounceTarget debounceTarget() {
            return new MemoryDebounceTarget();
        }
    }

    static class MemoryDebounceTarget {

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
    }
}