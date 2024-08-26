package com.thirdparty.ticketing.waiting.redis.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.testcontainer.RedisTestContainerStarter;

@SpringBootTest
class RedisWaitingCounterTest extends RedisTestContainerStarter {

    @Autowired private RedisWaitingCounter waitingCounter;

    @Autowired private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Nested
    @DisplayName("다음 대기 순번 조회 시")
    class GetNextCountTest {

        private String email;
        private long performanceId;

        @BeforeEach
        void setUp() {
            email = "email@email.com";
            performanceId = 1;
        }

        @Test
        @DisplayName("순번을 조회한다.")
        void getCount() {
            // given

            // when
            long nextCount = waitingCounter.getNextCount(performanceId);

            // then
            assertThat(nextCount).isEqualTo(1);
        }

        @Test
        @DisplayName("동시 요청 상황에서 순번을 순차적으로 조회한다.")
        void getCountIncrement() throws InterruptedException {
            // given
            long performanceId = 1;

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
        void noSharedWaitingCounter() {
            // given
            long performanceAId = 1;
            int performanceAWaitedMemberCount = 5;
            for (int i = 0; i < performanceAWaitedMemberCount; i++) {
                waitingCounter.getNextCount(performanceAId);
            }

            long performanceBId = 2;
            int performanceBWaitedMemberCount = 10;
            for (int i = 0; i < performanceBWaitedMemberCount; i++) {
                waitingCounter.getNextCount(performanceBId);
            }

            // when
            long performanceANextCount = waitingCounter.getNextCount(performanceAId);
            long performanceBNextCount = waitingCounter.getNextCount(performanceBId);

            // then
            assertThat(performanceANextCount).isNotEqualTo(performanceBNextCount);
            assertThat(performanceANextCount).isEqualTo(performanceAWaitedMemberCount + 1);
            assertThat(performanceBNextCount).isEqualTo(performanceBWaitedMemberCount + 1);
        }
    }
}
