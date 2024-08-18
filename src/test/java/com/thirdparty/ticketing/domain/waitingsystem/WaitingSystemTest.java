package com.thirdparty.ticketing.domain.waitingsystem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.global.waitingsystem.redis.TestRedisConfig;
import com.thirdparty.ticketing.support.SpyEventPublisher;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
@Import(TestRedisConfig.class)
class WaitingSystemTest extends TestContainerStarter {

    private WaitingSystem waitingSystem;

    @Autowired private WaitingManager waitingManager;

    @Autowired private RunningManager runningManager;

    private SpyEventPublisher eventPublisher;

    @Autowired private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> rawRunningCounter;

    @BeforeEach
    void setUp() {
        rawRunningCounter = redisTemplate.opsForValue();
        eventPublisher = new SpyEventPublisher();
        waitingSystem = new WaitingSystem(waitingManager, runningManager, eventPublisher);
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getRunningCounterKey(long performanceId) {
        return "running_counter:" + performanceId;
    }

    @Nested
    @DisplayName("사용자의 남은 순번 조회 시")
    class GetRemainingCountTest {

        @ParameterizedTest
        @CsvSource({"0, 0, 1", "15, 10, 6"})
        @DisplayName("자신이 몇 번째 차례인지 반환한다.")
        void getRemainingCount(int waitingCount, String runningCount, int expected) {
            // given
            long performanceId = 1;
            for (int i = 0; i < waitingCount; i++) {
                waitingSystem.enterWaitingRoom("email" + i + "@email.com", performanceId);
            }
            String email = "email@email.com";
            waitingSystem.enterWaitingRoom(email, performanceId);
            rawRunningCounter.set(getRunningCounterKey(performanceId), runningCount);

            // when
            long remainingCount = waitingSystem.getRemainingCount(email, performanceId);

            // then
            assertThat(remainingCount).isEqualTo(expected);
        }

        @Test
        @DisplayName("폴링 이벤트를 발행한다.")
        void publishPollingEvent() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            waitingSystem.enterWaitingRoom(email, performanceId);

            // when
            waitingSystem.getRemainingCount(email, performanceId);

            // then
            assertThat(eventPublisher.counter).hasValue(1);
        }
    }
}
