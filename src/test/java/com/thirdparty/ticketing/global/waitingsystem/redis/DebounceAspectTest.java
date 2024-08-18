package com.thirdparty.ticketing.global.waitingsystem.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.global.waitingsystem.Debounce;
import com.thirdparty.ticketing.global.waitingsystem.redis.DebounceAspectTest.TestConfig;
import com.thirdparty.ticketing.support.TestContainerStarter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class DebounceAspectTest extends TestContainerStarter {

    @Autowired
    private DebounceTarget debounceTarget;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DebounceTarget debounceTarget() {
            return new DebounceTarget();
        }
    }

    static class DebounceTarget {

        private final AtomicInteger counter;

        public DebounceTarget() {
            counter = new AtomicInteger(0);
        }

        @Debounce
        public void increment() {
            counter.incrementAndGet();
        }

        public int get() {
            return counter.get();
        }
    }


    @Nested
    @DisplayName("디바운스 aop 적용 시")
    class DebounceTest {

        @Test
        @DisplayName("동시에 한 번만 실행된다.")
        void debounce() throws InterruptedException {
            //given
            int poolSize = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
            CountDownLatch latch = new CountDownLatch(poolSize);

            //when
            for (int i = 0; i < poolSize; i++) {
                executorService.execute(() -> {
                    try {
                        debounceTarget.increment();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            //then
            assertThat(debounceTarget.get()).isEqualTo(1);
        }
    }
}
