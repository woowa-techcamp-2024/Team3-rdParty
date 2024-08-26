package com.thirdparty.ticketing.waiting.memory.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemoryWaitingCounterTest {

    private MemoryWaitingCounter waitingCounter;

    @Nested
    @DisplayName("다음 대기 순번 조회 시")
    class GetNextCountTest {

        @BeforeEach
        void setUp() {
            ConcurrentMap<Long, AtomicLong> counter = new ConcurrentHashMap<>();
            waitingCounter = new MemoryWaitingCounter(counter);
        }

        @Test
        @DisplayName("순번을 조회한다.")
        void getCount() {
            // given
            long performanceId = 1L;

            // when
            long nextCount = waitingCounter.getNextCount(performanceId);

            // then
            assertThat(nextCount).isEqualTo(1);
        }

        @Test
        @DisplayName("동시 요청 상황에서 순번을 순차적으로 조회한다.")
        void getCountIncrement() throws InterruptedException {
            // given
            long performanceId = 1L;

            int poolSize = 50;
            CountDownLatch latch = new CountDownLatch(poolSize);
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

            // when
            for (int i = 0; i < poolSize; i++) {
                int finalI = i;
                executorService.execute(
                        () -> {
                            try {
                                String email = "email" + finalI + "@email.com";
                                waitingCounter.getNextCount(performanceId);
                            } finally {
                                latch.countDown();
                            }
                        });
            }
            latch.await();

            // then
            assertThat(waitingCounter.getNextCount(performanceId)).isEqualTo(poolSize + 1);
        }

        @Test
        @DisplayName("각 공연은 대기 순번을 공유하지 않는다.")
        void noShareWaitingCounter() {
            // given
            long performanceId = 1L;
            int count = 5;
            for (int i = 0; i < count; i++) {
                waitingCounter.getNextCount(performanceId);
            }

            long performanceId2 = 2L;
            int count2 = 10;
            for (int i = 0; i < count2; i++) {
                waitingCounter.getNextCount(performanceId2);
            }

            // when
            long performanceANextCount = waitingCounter.getNextCount(performanceId);
            long performanceBNextCount = waitingCounter.getNextCount(performanceId2);

            // then
            assertThat(performanceANextCount).isNotEqualTo(performanceBNextCount);
            assertThat(performanceANextCount).isEqualTo(count + 1);
            assertThat(performanceBNextCount).isEqualTo(count2 + 1);
        }
    }
}
