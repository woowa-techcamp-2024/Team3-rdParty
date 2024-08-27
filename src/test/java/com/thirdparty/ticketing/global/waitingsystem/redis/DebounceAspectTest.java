package com.thirdparty.ticketing.global.waitingsystem.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.support.BaseIntegrationTest;
import com.thirdparty.ticketing.support.integration.AspectTestConfig.DebounceTarget;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DebounceAspectTest extends BaseIntegrationTest {

    @Autowired
    private DebounceTarget debounceTarget;

    @Nested
    @DisplayName("디바운스 aop 적용 시")
    class DebounceTest {

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
                                debounceTarget.increment();
                            } finally {
                                latch.countDown();
                            }
                        });
            }
            latch.await();

            // then
            assertThat(debounceTarget.get()).isEqualTo(1);
        }
    }
}
