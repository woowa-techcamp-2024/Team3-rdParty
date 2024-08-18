package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.thirdparty.ticketing.global.waitingsystem.redis.TestRedisConfig;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
@Import(TestRedisConfig.class)
class RedisRunningCounterTest extends TestContainerStarter {

    @Autowired private RedisRunningCounter runningCounter;

    @Autowired private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> rawRunningCounter;

    @BeforeEach
    void setUp() {
        rawRunningCounter = redisTemplate.opsForValue();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getRunningCounterKey(long performanceId) {
        return "running_counter:" + performanceId;
    }

    @Nested
    @DisplayName("작업 대기 공간으로 이동한 인원 수 조회 시")
    class GetRunningCountTest {

        @Test
        @DisplayName("카운터가 초기화되지 않았다면 0으로 초기화한다.")
        void initializeCounter() {
            // given
            long performanceId = 1;

            // when
            long runningCount = runningCounter.getRunningCount(performanceId);

            // then
            assertThat(runningCount).isEqualTo(0);
        }

        @RepeatedTest(5)
        @DisplayName("카운터가 초기화되었다면 0으로 초기화하지 않는다.")
        void doNotReInitializeCounter() throws InterruptedException, ExecutionException {
            // given
            long performanceId = 1;
            int poolSize = 50;
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
            CountDownLatch latch = new CountDownLatch(poolSize);

            // when
            for (int i = 0; i < poolSize; i++) {
                int finalI = i;
                executorService.execute(
                        () -> {
                            try {
                                if (finalI % 2 == 0) {
                                    rawRunningCounter.set(
                                            getRunningCounterKey(performanceId), "10");
                                } else {
                                    runningCounter.getRunningCount(performanceId);
                                }
                            } finally {
                                latch.countDown();
                            }
                        });
            }
            latch.await();

            // then
            long runningCount = runningCounter.getRunningCount(performanceId);
            assertThat(runningCount).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("카운터 증가 호출 시")
    class IncrementTest {

        @Test
        @DisplayName("주어진 값만큼 값을 증가시킨다.")
        void increment() {
            //given
            long performanceId = 1;
            int number = 10;

            //when
            runningCounter.increment(performanceId, number);

            //then
            long runningCount = runningCounter.getRunningCount(performanceId);
            assertThat(runningCount).isEqualTo(number);
        }
    }
}
